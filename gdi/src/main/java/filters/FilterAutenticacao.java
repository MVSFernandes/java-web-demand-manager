package filters;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DAOAnexo;
import dao.DAOCategoria;
import dao.DAOComentario;
import dao.DAODemanda;
import dao.DAOLogin;
import dao.DAONotificacao;
import dao.DAOSetor;
import dao.DAOStatus;
import dao.DAOUsuario;
import models.Anexos;
import models.AtividadeDashboard;
import models.Comentarios;
import models.Demandas;
import models.EventoDemanda;
import models.Usuarios;

@WebFilter(urlPatterns = { "/views/*" })
public class FilterAutenticacao implements Filter {
    private static final String COOKIE_LEMBRAR = "GDI_REMEMBER";

    public FilterAutenticacao() { }
    public void destroy() { }
    public void init(FilterConfig fConfig) throws ServletException { }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest  req  = (HttpServletRequest)  request;
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpSession sessao = req.getSession(false);

            Usuarios usuarioLogado = (sessao != null)
                    ? (Usuarios) sessao.getAttribute("UsuarioLogado") : null;

            if (usuarioLogado == null) {
                // Rebuild the session from the remember-me token before blocking protected views.
                usuarioLogado = autenticarPorCookie(req, resp);
            }

            if (usuarioLogado == null) {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
                return;
            }

            String uri = req.getRequestURI();
            boolean isAdmin = "admin".equals(usuarioLogado.getPerfilUsuario());
            boolean isGerente = "gerente".equals(usuarioLogado.getPerfilUsuario());
            boolean isUsuario = "usuario".equals(usuarioLogado.getPerfilUsuario());

            if (isUsuario) {
                boolean paginaRestrita =
                    uri.contains("/usuarios.jsp")   ||
                    uri.contains("/setores.jsp")    ||
                    uri.contains("/categorias.jsp") ||
                    uri.contains("/status.jsp");

                if (paginaRestrita) {
                    req.getSession().setAttribute("Msg",
                        "Acesso negado! Apenas administradores e gerentes podem acessar esta area.");
                    resp.sendRedirect(req.getContextPath() + "/views/principal.jsp");
                    return;
                }
            }

            if (!isAdmin && uri.contains("/usuarios.jsp")) {
                req.getSession().setAttribute("Msg",
                    "Acesso negado! Apenas administradores podem acessar usuarios.");
                resp.sendRedirect(req.getContextPath() + "/views/principal.jsp");
                return;
            }

            // Protected JSPs share these session lists, so the filter keeps page loading consistent.
            prepararDados(req, usuarioLogado, isAdmin || isGerente);
            chain.doFilter(request, response);

        } catch (Exception e) {
            request.setAttribute("Msg", e.getMessage());
            request.getRequestDispatcher("/Erro.jsp").forward(request, response);
        }
    }

    private Usuarios autenticarPorCookie(HttpServletRequest req, HttpServletResponse resp) {
        String token = lerCookie(req, COOKIE_LEMBRAR);
        if (token == null || token.trim().isEmpty()) return null;
        try {
            Usuarios usuario = new DAOLogin().autenticarPorToken(token);
            if (usuario != null) {
                HttpSession sessao = req.getSession(true);
                sessao.setAttribute("UsuarioLogado", usuario);
                sessao.setAttribute("NomeUsuario", usuario.getNomeUsuario());
                return usuario;
            }
        } catch (Exception e) {
            apagarCookie(resp, req.getContextPath());
        }
        return null;
    }

    private String lerCookie(HttpServletRequest request, String nome) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (nome.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private void apagarCookie(HttpServletResponse response, String contextPath) {
        Cookie cookie = new Cookie(COOKIE_LEMBRAR, "");
        cookie.setHttpOnly(true);
        cookie.setPath(contextPath == null || contextPath.isEmpty() ? "/" : contextPath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void prepararDados(HttpServletRequest req, Usuarios usuarioLogado, boolean podeVerTudo) throws Exception {
        HttpSession session = req.getSession();
        String path = req.getServletPath();

        DAODemanda daoDemanda = new DAODemanda();
        List<Demandas> demandas = podeVerTudo
            ? daoDemanda.listarTodasDemandas()
            : daoDemanda.listarDemandasPorSolicitante(usuarioLogado.getIdUsuario());

        session.setAttribute("ListaDemandas", demandas);
        prepararNotificacoes(session, usuarioLogado, demandas);

        if (path.contains("principal.jsp")) {
            int periodoDias = lerPeriodoDias(req);
            List<Demandas> demandasPainel = filtrarPorPeriodo(demandas, periodoDias);
            req.setAttribute("ListaDemandasPainel", demandasPainel);
            req.setAttribute("periodoDias", periodoDias);
            req.setAttribute("periodoLabel", periodoDias == 0 ? "Todo o periodo" : "Ultimos " + periodoDias + " dias");
            aplicarIndicadores(req, demandasPainel);
            prepararAtividades(req, demandasPainel);
        }

        if (podeVerTudo || path.contains("demandas.jsp")) {
            session.setAttribute("ListaSetores", new DAOSetor().listarTodosSetores());
            session.setAttribute("ListaCategorias", new DAOCategoria().listarTodasCategorias());
            session.setAttribute("ListaStatus", new DAOStatus().listarTodosStatus());
        }

        if ("admin".equals(usuarioLogado.getPerfilUsuario()) || path.contains("usuarios.jsp")) {
            DAOUsuario daoUsuario = new DAOUsuario();
            session.setAttribute("ListaUsuarios", daoUsuario.listarTodosUsuarios());
        }

        if (podeVerTudo || path.contains("setores.jsp")) {
            DAOUsuario daoUsuario = new DAOUsuario();
            session.setAttribute("ListaGerentes", daoUsuario.listarGerentes());
        }

        if (path.contains("comentarios.jsp")) {
            prepararComentarios(req, session, daoDemanda);
        }
    }

    private void prepararNotificacoes(HttpSession session, Usuarios usuarioLogado, List<Demandas> demandas) throws Exception {
        List<Comentarios> recentes = new DAOComentario().listarComentariosRecentes(5);
        List<Anexos> anexosRecentes = new DAOAnexo().listarAnexosRecentes(5);
        // Stable keys let read markers survive even as notification lists are rebuilt.
        Set<String> atuais = new HashSet<>();
        Set<Integer> idsDemandasVisiveis = new HashSet<>();
        for (Demandas d : demandas) {
            idsDemandasVisiveis.add(d.getIdDemanda());
            if ("vencido".equals(d.getSlaStatus())) {
                atuais.add("sla:" + d.getIdDemanda());
            }
        }
        for (Comentarios comentario : recentes) {
            if (idsDemandasVisiveis.contains(comentario.getIdDemandaComentario())) {
                atuais.add("comentario:" + comentario.getIdComentario());
            }
        }
        List<Anexos> anexosVisiveis = new ArrayList<>();
        for (Anexos anexo : anexosRecentes) {
            if (idsDemandasVisiveis.contains(anexo.getIdDemandaAnexo())) {
                atuais.add("anexo:" + anexo.getIdAnexo());
                anexosVisiveis.add(anexo);
            }
        }

        Set<String> lidas = new DAONotificacao().listarChavesLidas(usuarioLogado.getIdUsuario());

        int naoLidas = 0;
        for (String chave : atuais) {
            if (!lidas.contains(chave)) {
                naoLidas++;
            }
        }

        session.setAttribute("ListaNotificacoesComentarios", recentes);
        session.setAttribute("ListaNotificacoesAnexos", anexosVisiveis);
        session.setAttribute("NotificacoesAtuais", atuais);
        session.setAttribute("TotalNotificacoesAtuais", atuais.size());
        session.setAttribute("TotalDemandasVencidas", atuais.stream().filter(chave -> chave.startsWith("sla:")).count());
        session.setAttribute("TotalNotificacoes", naoLidas);
    }

    private void prepararComentarios(HttpServletRequest req, HttpSession session, DAODemanda daoDemanda) throws Exception {
        Demandas detalhe = (Demandas) session.getAttribute("DemandaDetalhe");
        if (detalhe == null) detalhe = (Demandas) session.getAttribute("DemandaAtual");

        String idParam = req.getParameter("idDemanda");
        if ((detalhe == null || detalhe.getIdDemanda() == null) && idParam != null && !idParam.isEmpty()) {
            detalhe = daoDemanda.consultarDemanda(Integer.parseInt(idParam));
        }

        List<Comentarios> comentarios = new ArrayList<>();
        List<Anexos> anexos = new ArrayList<>();

        if (detalhe != null && detalhe.getIdDemanda() != null) {
            int idDemanda = detalhe.getIdDemanda();
            comentarios = new DAOComentario().listarComentariosPorDemanda(idDemanda);
            anexos = new DAOAnexo().listarAnexosPorDemanda(idDemanda);
            session.setAttribute("DemandaDetalhe", detalhe);
            session.setAttribute("DemandaAtual", detalhe);
            req.setAttribute("idDemandaAtual", idDemanda);
        }

        session.setAttribute("ListaComentarios", comentarios);
        session.setAttribute("ListaAnexos", anexos);
        session.setAttribute("ListaEventosDemanda", montarEventosDemanda(comentarios, anexos));
    }

    private List<EventoDemanda> montarEventosDemanda(List<Comentarios> comentarios, List<Anexos> anexos) {
        List<EventoDemanda> eventos = new ArrayList<>();

        for (Comentarios comentario : comentarios) {
            EventoDemanda evento = new EventoDemanda(
                "comentario",
                nomeOuSistema(comentario.getNomeUsuario()),
                comentario.getMensagemComentario(),
                "Comentario",
                comentario.getCriadoEmComentario()
            );
            evento.setIdAutor(comentario.getIdUsuarioComentario());
            evento.setIdComentario(comentario.getIdComentario());
            eventos.add(evento);
        }

        for (Anexos anexo : anexos) {
            EventoDemanda evento = new EventoDemanda(
                "anexo",
                nomeOuSistema(anexo.getNomeUsuarioUpload()),
                "Anexou " + anexo.getNomeArquivoAnexo(),
                anexo.getTipoArquivoTexto() + " - " + anexo.getTamanhoFormatado(),
                anexo.getCriadoEmAnexo()
            );
            evento.setIdAutor(anexo.getIdUsuarioUploadAnexo());
            evento.setIdAnexo(anexo.getIdAnexo());
            eventos.add(evento);
        }

        Collections.sort(eventos);
        return eventos;
    }

    private void aplicarIndicadores(HttpServletRequest req, List<Demandas> demandas) {
        int abertas = 0;
        int concluidas = 0;
        int vencidas = 0;
        int alta = 0;
        int media = 0;
        int baixa = 0;

        for (Demandas d : demandas) {
            String status = d.getNomeStatus();
            if (d.getIdStatusDemanda() != null && d.getIdStatusDemanda() == 1 ||
                "Aberta".equalsIgnoreCase(status)) {
                abertas++;
            }
            if (d.getIdStatusDemanda() != null && d.getIdStatusDemanda() == 4 ||
                "Concluida".equalsIgnoreCase(status)) {
                concluidas++;
            }
            if ("vencido".equals(d.getSlaStatus())) {
                vencidas++;
            }
            if (d.getPrioridadeDemanda() == 1) {
                alta++;
            } else if (d.getPrioridadeDemanda() == 3) {
                baixa++;
            } else {
                media++;
            }
        }

        int total = demandas.size();
        req.setAttribute("totalDemandas", demandas.size());
        req.setAttribute("totalAbertas", abertas);
        req.setAttribute("totalConcluidas", concluidas);
        req.setAttribute("totalVencidas", vencidas);
        req.setAttribute("totalAlta", alta);
        req.setAttribute("totalMedia", media);
        req.setAttribute("totalBaixa", baixa);
        req.setAttribute("percentAlta", total == 0 ? 0 : Math.round((alta * 100.0f) / total));
        req.setAttribute("percentMedia", total == 0 ? 0 : Math.round((media * 100.0f) / total));
        req.setAttribute("percentBaixa", total == 0 ? 0 : Math.round((baixa * 100.0f) / total));
        req.setAttribute("percentAbertas", total == 0 ? 0 : Math.round((abertas * 100.0f) / total));
        req.setAttribute("percentConcluidas", total == 0 ? 0 : Math.round((concluidas * 100.0f) / total));
        req.setAttribute("percentVencidas", total == 0 ? 0 : Math.round((vencidas * 100.0f) / total));
    }

    private void prepararAtividades(HttpServletRequest req, List<Demandas> demandas) throws Exception {
        List<AtividadeDashboard> atividades = new ArrayList<>();
        Set<Integer> idsVisiveis = new HashSet<>();
        AtividadeDashboard slaMaisRecente = null;
        AtividadeDashboard anexoMaisRecente = null;

        for (Demandas d : demandas) {
            idsVisiveis.add(d.getIdDemanda());

            if ("vencido".equals(d.getSlaStatus())) {
                AtividadeDashboard atividadeSla = new AtividadeDashboard(
                    "sla",
                    "SLA",
                    "venceu na demanda",
                    d.getIdDemanda(),
                    d.getTituloDemanda(),
                    detalheDemanda(d),
                    dataSla(d)
                );
                atividades.add(atividadeSla);
                if (slaMaisRecente == null || atividadeSla.compareTo(slaMaisRecente) < 0) {
                    slaMaisRecente = atividadeSla;
                }
            }

            if (d.getConcluidaEmDemanda() != null || statusConcluido(d)) {
                atividades.add(new AtividadeDashboard(
                    "concluida",
                    nomeOuSistema(d.getNomeSolicitante()),
                    "concluiu demanda",
                    d.getIdDemanda(),
                    d.getTituloDemanda(),
                    detalheDemanda(d),
                    dataOuAbertura(d.getConcluidaEmDemanda(), d)
                ));
            } else if (d.getAbertaEmDemanda() != null) {
                atividades.add(new AtividadeDashboard(
                    "aberta",
                    nomeOuSistema(d.getNomeSolicitante()),
                    "abriu demanda",
                    d.getIdDemanda(),
                    d.getTituloDemanda(),
                    detalheDemanda(d),
                    d.getAbertaEmDemanda()
                ));
            }
        }

        for (Comentarios c : new DAOComentario().listarComentariosRecentes(8)) {
            if (!idsVisiveis.contains(c.getIdDemandaComentario())) {
                continue;
            }

            AtividadeDashboard atividadeComentario = new AtividadeDashboard(
                "comentario",
                nomeOuSistema(c.getNomeUsuario()),
                "comentou em",
                c.getIdDemandaComentario(),
                vazioParaTraco(c.getTituloDemanda()),
                vazioParaTraco(c.getTituloDemanda()),
                c.getCriadoEmComentario()
            );
            atividadeComentario.setIdComentario(c.getIdComentario());
            atividades.add(atividadeComentario);
        }

        for (Anexos a : new DAOAnexo().listarAnexosRecentes(8)) {
            if (!idsVisiveis.contains(a.getIdDemandaAnexo())) {
                continue;
            }

            AtividadeDashboard atividadeAnexo = new AtividadeDashboard(
                "anexo",
                nomeOuSistema(a.getNomeUsuarioUpload()),
                "anexou arquivo em",
                a.getIdDemandaAnexo(),
                a.getNomeArquivoAnexo(),
                a.getTipoArquivoTexto() + " - " + a.getNomeArquivoAnexo(),
                a.getCriadoEmAnexo()
            );
            atividadeAnexo.setIdAnexo(a.getIdAnexo());
            atividades.add(atividadeAnexo);
            if (anexoMaisRecente == null || atividadeAnexo.compareTo(anexoMaisRecente) < 0) {
                anexoMaisRecente = atividadeAnexo;
            }
        }

        Collections.sort(atividades);
        if (atividades.size() > 4) {
            atividades = new ArrayList<>(atividades.subList(0, 4));
        }
        if (slaMaisRecente != null && !contemTipo(atividades, "sla")) {
            if (atividades.size() >= 4) {
                atividades.remove(atividades.size() - 1);
            }
            atividades.add(slaMaisRecente);
            Collections.sort(atividades);
        }
        if (anexoMaisRecente != null && !contemTipo(atividades, "anexo")) {
            if (atividades.size() >= 4) {
                // Keep one SLA item visible when adding the latest attachment to the capped feed.
                removerUltimoSemTipo(atividades, "sla");
            }
            atividades.add(anexoMaisRecente);
            Collections.sort(atividades);
        }

        req.setAttribute("ListaAtividades", atividades);
        req.setAttribute("dashboardAtualizadoTexto", atividades.isEmpty() ? "agora" : atividades.get(0).getTempoRelativo());
    }

    private int lerPeriodoDias(HttpServletRequest req) {
        String periodo = req.getParameter("periodo");
        if ("7".equals(periodo)) return 7;
        if ("90".equals(periodo)) return 90;
        if ("todos".equals(periodo)) return 0;
        return 30;
    }

    private List<Demandas> filtrarPorPeriodo(List<Demandas> demandas, int periodoDias) {
        if (periodoDias <= 0) {
            return demandas;
        }

        LocalDateTime limite = LocalDateTime.now().minusDays(periodoDias);
        List<Demandas> filtradas = new ArrayList<>();

        for (Demandas demanda : demandas) {
            Timestamp data = demanda.getAbertaEmDemanda();
            if (data != null && !data.toLocalDateTime().isBefore(limite)) {
                filtradas.add(demanda);
            }
        }

        return filtradas;
    }

    private boolean contemTipo(List<AtividadeDashboard> atividades, String tipo) {
        for (AtividadeDashboard atividade : atividades) {
            if (tipo.equals(atividade.getTipo())) {
                return true;
            }
        }
        return false;
    }

    private void removerUltimoSemTipo(List<AtividadeDashboard> atividades, String tipoProtegido) {
        for (int i = atividades.size() - 1; i >= 0; i--) {
            if (!tipoProtegido.equals(atividades.get(i).getTipo())) {
                atividades.remove(i);
                return;
            }
        }
        atividades.remove(atividades.size() - 1);
    }

    private String detalheDemanda(Demandas d) {
        String setor = vazioParaTraco(d.getNomeSetorDestino());
        String titulo = vazioParaTraco(d.getTituloDemanda());
        if ("-".equals(setor)) return titulo;
        return setor + " - " + titulo;
    }

    private String nomeOuSistema(String nome) {
        return (nome == null || nome.trim().isEmpty()) ? "Sistema" : nome;
    }

    private String vazioParaTraco(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? "-" : valor;
    }

    private boolean statusConcluido(Demandas d) {
        String status = d.getNomeStatus();
        return status != null && status.toLowerCase().contains("conclu");
    }

    private Timestamp dataOuAbertura(Timestamp data, Demandas d) {
        return data != null ? data : d.getAbertaEmDemanda();
    }

    private Timestamp dataSla(Demandas d) {
        if (d.getSlaDataLimiteDemanda() != null) {
            return d.getSlaDataLimiteDemanda();
        }
        return d.getAbertaEmDemanda();
    }
}
