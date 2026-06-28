package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOLogin;
import models.Usuarios;

@WebServlet("/ServletLogin")
public class ServletLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String COOKIE_LEMBRAR = "GDI_REMEMBER";
    private static final int DIAS_LEMBRAR = 30;

    private DAOLogin daoLogin = new DAOLogin();

    public ServletLogin() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuarios usuario = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
        String token = lerCookie(request, COOKIE_LEMBRAR);
        try {
            if (token != null) daoLogin.removerToken(token);
            if (usuario != null) daoLogin.removerTokensDoUsuario(usuario.getIdUsuario());
        } catch (Exception e) {
            request.getSession().setAttribute("Msg", "Nao foi possivel limpar todos os tokens de acesso.");
        }
        request.getSession().invalidate();
        apagarCookie(response, request.getContextPath());
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String txtEmail = request.getParameter("txtEmail");
        String txtSenha = request.getParameter("txtSenha");

        if (txtEmail != null && !txtEmail.isEmpty() &&
                txtSenha != null && !txtSenha.isEmpty()) {
            try {
                Usuarios usu = daoLogin.verificarLogin(txtEmail, txtSenha);

                if (usu != null) {
                    if (usu.getAtivoUsuario() == 0) {
                        apagarCookie(response, request.getContextPath());
                        request.setAttribute("Msg", "Usuario desativado. Procure um administrador.");
                        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
                        rd.forward(request, response);
                        return;
                    }
                    request.getSession().setAttribute("UsuarioLogado", usu);
                    request.getSession().setAttribute("NomeUsuario", usu.getNomeUsuario());
                    if ("on".equals(request.getParameter("lembrar"))) {
                        // The browser receives the raw token; DAOLogin stores only its hash.
                        String token = daoLogin.criarTokenPersistente(usu.getIdUsuario(), DIAS_LEMBRAR);
                        gravarCookie(response, request.getContextPath(), token);
                    } else {
                        apagarCookie(response, request.getContextPath());
                    }
                    response.sendRedirect(request.getContextPath() + "/views/principal.jsp");
                } else {
                    request.setAttribute("Msg", "E-mail ou senha incorretos!");
                    RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
                    rd.forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("Msg", "Erro de sistema: " + e.getMessage());
                RequestDispatcher rd = request.getRequestDispatcher("Erro.jsp");
                rd.forward(request, response);
            }
        } else {
            request.setAttribute("Msg", "Informe o e-mail e a senha!");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
        }
    }

    private String lerCookie(HttpServletRequest request, String nome) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (nome.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private void gravarCookie(HttpServletResponse response, String contextPath, String token) {
        Cookie cookie = new Cookie(COOKIE_LEMBRAR, token);
        cookie.setHttpOnly(true);
        cookie.setPath(contextPath == null || contextPath.isEmpty() ? "/" : contextPath);
        cookie.setMaxAge(DIAS_LEMBRAR * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    private void apagarCookie(HttpServletResponse response, String contextPath) {
        Cookie cookie = new Cookie(COOKIE_LEMBRAR, "");
        cookie.setHttpOnly(true);
        cookie.setPath(contextPath == null || contextPath.isEmpty() ? "/" : contextPath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
