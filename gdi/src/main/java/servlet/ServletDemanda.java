package servlet;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAODemanda;
import models.Demandas;
import models.Usuarios;
import util.ValidacaoException;

@WebServlet("/ServletDemanda")
public class ServletDemanda extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAODemanda daoDemanda = new DAODemanda();

    public ServletDemanda() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoDemanda.excluirDemanda(id);
                request.getSession().setAttribute("Msg", "Demanda excluida com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/demandas.jsp");

            } else if ("carregar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Demandas dem = daoDemanda.consultarDemanda(id);
                request.getSession().setAttribute("Demanda", dem);
                response.sendRedirect(request.getContextPath() + "/views/demandas.jsp");

            } else if ("comentarios".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Demandas dem = daoDemanda.consultarDemanda(id);
                request.getSession().setAttribute("DemandaAtual", dem);
                request.getSession().setAttribute("DemandaDetalhe", dem);
                String focoAnexo = request.getParameter("anexo");
                String focoComentario = request.getParameter("comentario");
                String destino = request.getContextPath() + "/views/comentarios.jsp";
                if (focoAnexo != null && !focoAnexo.isEmpty()) {
                    destino += "?anexo=" + focoAnexo;
                } else if (focoComentario != null && !focoComentario.isEmpty()) {
                    destino += "?comentario=" + focoComentario;
                }
                response.sendRedirect(destino);

            } else {
                response.sendRedirect(request.getContextPath() + "/views/demandas.jsp");
            }
        } catch (ValidacaoException e) {
            request.getSession().setAttribute("Msg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/views/demandas.jsp");
        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            String titulo = request.getParameter("txtTitulo");
            String descricao = request.getParameter("txtDescricao");
            String prioridade = request.getParameter("txtPrioridade");
            String sla = request.getParameter("txtSla");
            String idSetor = request.getParameter("idSetorDestino");
            String idCategoria = request.getParameter("idCategoria");
            String idStatus = request.getParameter("idStatus");

            Usuarios usuLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
            int idSolicitante = usuLogado != null ? usuLogado.getIdUsuario() : 1;

            Demandas dem = new Demandas();
            dem.setIdDemanda(id != null && !id.isEmpty() ? Integer.parseInt(id) : null);
            dem.setTituloDemanda(titulo);
            dem.setDescricaoDemanda(descricao);
            dem.setPrioridadeDemanda(prioridade != null && !prioridade.isEmpty() ? Integer.parseInt(prioridade) : 2);
            dem.setSlaDataLimiteDemanda(sla != null && !sla.isEmpty() ? Timestamp.valueOf(sla + " 23:59:59") : null);
            dem.setIdSolicitanteDemanda(idSolicitante);
            dem.setIdSetorDestinoDemanda(idSetor != null && !idSetor.isEmpty() ? Integer.parseInt(idSetor) : null);
            dem.setIdCategoriaDemanda(idCategoria != null && !idCategoria.isEmpty() ? Integer.parseInt(idCategoria) : null);
            dem.setIdStatusDemanda(idStatus != null && !idStatus.isEmpty() ? Integer.parseInt(idStatus) : 1);

            String msg = dem.isNova() ? "Demanda aberta com sucesso!" : "Demanda atualizada com sucesso!";
            daoDemanda.gravarDemanda(dem, idSolicitante);
            request.getSession().setAttribute("Msg", msg);
            response.sendRedirect(request.getContextPath() + "/views/demandas.jsp");

        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }
}
