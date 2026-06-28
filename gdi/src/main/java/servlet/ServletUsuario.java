package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOUsuario;
import models.Usuarios;
import util.ValidacaoException;

@WebServlet("/ServletUsuario")
public class ServletUsuario extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAOUsuario daoUsuario = new DAOUsuario();

    public ServletUsuario() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoUsuario.excluirUsuario(id);
                request.getSession().setAttribute("Msg", "Usuario excluido com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");

            } else if ("desativar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoUsuario.alterarAtivoUsuario(id, 0);
                request.getSession().setAttribute("Msg", "Usuario desativado com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");

            } else if ("reativar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoUsuario.alterarAtivoUsuario(id, 1);
                request.getSession().setAttribute("Msg", "Usuario reativado com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");

            } else if ("carregar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Usuarios user = daoUsuario.consultarUsuario(id);
                request.getSession().setAttribute("User", user);
                response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");

            } else {
                response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");
            }

        } catch (ValidacaoException e) {
            request.getSession().setAttribute("Msg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");
        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String id     = request.getParameter("id");
            String nome   = request.getParameter("txtNome");
            String email  = request.getParameter("txtEmail");
            String senha  = request.getParameter("txtSenha");
            String perfil = request.getParameter("txtPerfil");
            String idSetor = request.getParameter("idSetor");

            Usuarios user = new Usuarios();
            user.setIdUsuario(id != null && !id.isEmpty() ? Integer.parseInt(id) : null);
            user.setNomeUsuario(nome);
            user.setEmailUsuario(email);
            user.setSenhaHashUsuario(senha);
            user.setPerfilUsuario(perfil == null ? "usuario" : perfil);
            user.setAtivoUsuario(1);
            user.setIdSetorUsuario(idSetor != null && !idSetor.isEmpty() ? Integer.parseInt(idSetor) : null);

            String msg = user.isNovo() ? "Usuario cadastrado com sucesso!" : "Usuario atualizado com sucesso!";
            daoUsuario.gravarUsuario(user);
            request.getSession().setAttribute("Msg", msg);
            response.sendRedirect(request.getContextPath() + "/views/usuarios.jsp");

        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }
}
