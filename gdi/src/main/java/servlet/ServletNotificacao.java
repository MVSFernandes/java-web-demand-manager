package servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DAONotificacao;
import models.Usuarios;

@WebServlet("/ServletNotificacao")
public class ServletNotificacao extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            @SuppressWarnings("unchecked")
            Set<String> atuais = (Set<String>) session.getAttribute("NotificacoesAtuais");

            Usuarios usuarioLogado = (Usuarios) session.getAttribute("UsuarioLogado");
            if (usuarioLogado != null && usuarioLogado.getIdUsuario() != null && atuais != null) {
                try {
                    new DAONotificacao().marcarComoLidas(usuarioLogado.getIdUsuario(), atuais);
                } catch (Exception e) {
                    throw new ServletException("Erro ao marcar notificacoes como lidas: " + e.getMessage(), e);
                }
            }

            session.setAttribute("TotalNotificacoes", 0);
        }

        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            referer = request.getContextPath() + "/views/principal.jsp";
        }
        response.sendRedirect(referer);
    }
}
