package servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAONotificacao;
import models.Usuarios;

@WebServlet("/ServletAbrirNotificacao")
public class ServletAbrirNotificacao extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuarios usuarioLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String chave = request.getParameter("chave");
        if (chave != null && !chave.isEmpty()) {
            try {
                Set<String> chaves = new HashSet<>();
                chaves.add(chave);
                new DAONotificacao().marcarComoLidas(usuarioLogado.getIdUsuario(), chaves);
            } catch (Exception e) {
                throw new ServletException("Erro ao abrir notificacao: " + e.getMessage(), e);
            }
        }

        String idDemanda = request.getParameter("idDemanda");
        if (idDemanda == null || idDemanda.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/views/principal.jsp");
            return;
        }

        String destino = request.getContextPath()
            + "/ServletDemanda?acao=comentarios&id=" + idDemanda;

        String idAnexo = request.getParameter("idAnexo");
        String idComentario = request.getParameter("idComentario");
        if (idAnexo != null && !idAnexo.isEmpty()) {
            destino += "&anexo=" + idAnexo;
        } else if (idComentario != null && !idComentario.isEmpty()) {
            destino += "&comentario=" + idComentario;
        }

        response.sendRedirect(destino);
    }
}
