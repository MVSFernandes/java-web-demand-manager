package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOAnexo;
import dao.DAODemanda;
import models.Demandas;
import models.Usuarios;

@WebServlet("/ServletExportarDashboard")
public class ServletExportarDashboard extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuarios usuarioLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            int periodoDias = lerPeriodoDias(request);
            String periodoLabel = periodoDias == 0 ? "Todo o periodo" : "Ultimos " + periodoDias + " dias";
            boolean podeVerTudo = "admin".equals(usuarioLogado.getPerfilUsuario())
                || "gerente".equals(usuarioLogado.getPerfilUsuario());

            DAODemanda daoDemanda = new DAODemanda();
            List<Demandas> demandas = podeVerTudo
                ? daoDemanda.listarTodasDemandas()
                : daoDemanda.listarDemandasPorSolicitante(usuarioLogado.getIdUsuario());
            demandas = filtrarPorPeriodo(demandas, periodoDias);

            int abertas = 0;
            int concluidas = 0;
            int vencidas = 0;
            for (Demandas demanda : demandas) {
                String status = demanda.getNomeStatus();
                if ((demanda.getIdStatusDemanda() != null && demanda.getIdStatusDemanda() == 1)
                        || "Aberta".equalsIgnoreCase(status)) {
                    abertas++;
                }
                if ((demanda.getIdStatusDemanda() != null && demanda.getIdStatusDemanda() == 4)
                        || (status != null && status.toLowerCase().contains("conclu"))) {
                    concluidas++;
                }
                if ("vencido".equals(demanda.getSlaStatus())) {
                    vencidas++;
                }
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"gdi-dashboard.xls\"");

            PrintWriter out = response.getWriter();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            out.println("\uFEFF<!DOCTYPE html>");
            out.println("<html><head><meta charset=\"UTF-8\" />");
            out.println("<style>");
            out.println("body{font-family:Arial,sans-serif;color:#1f2937}");
            out.println("table{border-collapse:collapse;width:100%}");
            out.println(".title{background:#0f172a;color:#fff;font-size:22px;font-weight:700;text-align:left}");
            out.println(".subtitle{background:#e5edf7;color:#334155;font-size:12px}");
            out.println(".summary-label{background:#1f2937;color:#fff;font-weight:700;text-align:center}");
            out.println(".summary-value{background:#f8fafc;color:#111827;font-size:20px;font-weight:700;text-align:center}");
            out.println("th{background:#e07b39;color:#111827;font-weight:700;text-align:left}");
            out.println("td,th{border:1px solid #cbd5e1;padding:8px;vertical-align:middle}");
            out.println(".id{width:70px}.titulo{width:280px}.prioridade{width:110px}.status{width:140px}.setor{width:170px}.categoria{width:190px}.solicitante{width:170px}.sla{width:90px}.anexos{width:90px}.data{width:150px}");
            out.println(".alta{background:#fee2e2;color:#991b1b;font-weight:700}.media{background:#fef3c7;color:#92400e;font-weight:700}.baixa{background:#dcfce7;color:#166534;font-weight:700}");
            out.println(".vencido{background:#fee2e2;color:#991b1b;font-weight:700}.ok{background:#dcfce7;color:#166534;font-weight:700}.urgente{background:#fef3c7;color:#92400e;font-weight:700}");
            out.println("</style></head><body>");

            out.println("<table>");
            out.println("<tr><td class=\"title\" colspan=\"10\">Relatorio do Painel GDI</td></tr>");
            out.println("<tr><td class=\"subtitle\" colspan=\"10\">Periodo: " + html(periodoLabel)
                + " | Gerado em: " + html(formato.format(new java.util.Date()))
                + " | Usuario: " + html(usuarioLogado.getNomeUsuario()) + "</td></tr>");
            out.println("<tr><td colspan=\"10\"></td></tr>");
            out.println("<tr>");
            out.println("<td class=\"summary-label\" colspan=\"2\">Total de demandas</td>");
            out.println("<td class=\"summary-label\" colspan=\"2\">Abertas</td>");
            out.println("<td class=\"summary-label\" colspan=\"2\">Concluidas</td>");
            out.println("<td class=\"summary-label\" colspan=\"4\">SLA vencido</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td class=\"summary-value\" colspan=\"2\">" + demandas.size() + "</td>");
            out.println("<td class=\"summary-value\" colspan=\"2\">" + abertas + "</td>");
            out.println("<td class=\"summary-value\" colspan=\"2\">" + concluidas + "</td>");
            out.println("<td class=\"summary-value\" colspan=\"4\">" + vencidas + "</td>");
            out.println("</tr>");
            out.println("<tr><td colspan=\"10\"></td></tr>");

            out.println("<tr>");
            out.println("<th class=\"id\">ID</th>");
            out.println("<th class=\"titulo\">Titulo</th>");
            out.println("<th class=\"prioridade\">Prioridade</th>");
            out.println("<th class=\"status\">Status</th>");
            out.println("<th class=\"setor\">Setor</th>");
            out.println("<th class=\"categoria\">Categoria</th>");
            out.println("<th class=\"solicitante\">Solicitante</th>");
            out.println("<th class=\"sla\">SLA</th>");
            out.println("<th class=\"anexos\">Anexos</th>");
            out.println("<th class=\"data\">Abertura</th>");
            out.println("</tr>");

            DAOAnexo daoAnexo = new DAOAnexo();
            for (Demandas d : demandas) {
                int totalAnexos = daoAnexo.listarAnexosPorDemanda(d.getIdDemanda()).size();
                out.println("<tr>");
                out.println("<td class=\"id\">" + html("#" + d.getIdDemanda()) + "</td>");
                out.println("<td class=\"titulo\">" + html(d.getTituloDemanda()) + "</td>");
                out.println("<td class=\"prioridade " + prioridadeCss(d) + "\">" + html(d.getPrioridadeTexto()) + "</td>");
                out.println("<td class=\"status\">" + html(d.getNomeStatus()) + "</td>");
                out.println("<td class=\"setor\">" + html(d.getNomeSetorDestino()) + "</td>");
                out.println("<td class=\"categoria\">" + html(d.getNomeCategoria()) + "</td>");
                out.println("<td class=\"solicitante\">" + html(d.getNomeSolicitante()) + "</td>");
                out.println("<td class=\"sla " + slaCss(d) + "\">" + html(d.getSlaTexto()) + "</td>");
                out.println("<td class=\"anexos\">" + totalAnexos + "</td>");
                out.println("<td class=\"data\">" + html(d.getAbertaEmDemanda() == null ? "" : formato.format(d.getAbertaEmDemanda())) + "</td>");
                out.println("</tr>");
            }

            if (demandas.isEmpty()) {
                out.println("<tr><td colspan=\"10\">Nenhuma demanda encontrada para o periodo selecionado.</td></tr>");
            }

            out.println("</table></body></html>");
        } catch (Exception e) {
            throw new ServletException("Erro ao exportar painel: " + e.getMessage(), e);
        }
    }

    private int lerPeriodoDias(HttpServletRequest request) {
        String periodo = request.getParameter("periodo");
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
            if (demanda.getAbertaEmDemanda() != null
                    && !demanda.getAbertaEmDemanda().toLocalDateTime().isBefore(limite)) {
                filtradas.add(demanda);
            }
        }
        return filtradas;
    }

    private String prioridadeCss(Demandas demanda) {
        if (demanda.getPrioridadeDemanda() == 1) return "alta";
        if (demanda.getPrioridadeDemanda() == 3) return "baixa";
        return "media";
    }

    private String slaCss(Demandas demanda) {
        String status = demanda.getSlaStatus();
        if ("vencido".equals(status)) return "vencido";
        if ("urgente".equals(status) || "atencao".equals(status)) return "urgente";
        return "ok";
    }

    private String html(Object valor) {
        String texto = valor == null ? "" : String.valueOf(valor);
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
