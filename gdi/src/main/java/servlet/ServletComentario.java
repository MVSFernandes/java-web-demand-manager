package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOComentario;
import models.Comentarios;
import models.Usuarios;

@WebServlet("/ServletComentario")
public class ServletComentario extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAOComentario daoComentario = new DAOComentario();

    public ServletComentario() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");
        String idDemanda = request.getParameter("idDemanda");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Comentarios comentario = daoComentario.consultarComentario(id);
                Usuarios usuarioLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
                boolean podeExcluir = comentario != null && usuarioLogado != null
                    && (usuarioLogado.getIdUsuario().equals(comentario.getIdUsuarioComentario())
                        || "admin".equals(usuarioLogado.getPerfilUsuario()));

                if (!podeExcluir) {
                    request.getSession().setAttribute("Msg", "ERRO: Voce nao tem permissao para excluir este comentario.");
                    response.sendRedirect(request.getContextPath() + "/views/comentarios.jsp?idDemanda=" + idDemanda);
                    return;
                }

                daoComentario.excluirComentario(id);
                request.getSession().setAttribute("Msg", "Comentario excluido!");
                response.sendRedirect(request.getContextPath() + "/views/comentarios.jsp?idDemanda=" + idDemanda);
            } else {
                response.sendRedirect(request.getContextPath() + "/views/demandas.jsp");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("Msg", "ERRO: Nao foi possivel excluir o comentario. " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/views/comentarios.jsp?idDemanda=" + idDemanda);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idDemanda = request.getParameter("idDemanda");
            String mensagem = request.getParameter("txtMensagem");

            Usuarios usuLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
            int idUsuario = usuLogado != null ? usuLogado.getIdUsuario() : 1;

            Comentarios com = new Comentarios(Integer.parseInt(idDemanda), idUsuario, mensagem);
            daoComentario.gravarComentario(com);
            request.getSession().setAttribute("Msg", "Comentario adicionado!");
            response.sendRedirect(request.getContextPath() + "/views/comentarios.jsp?idDemanda=" + idDemanda);

        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }
}
