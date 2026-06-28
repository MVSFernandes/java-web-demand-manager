package filters;

import java.io.IOException;

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

import dao.DAOLogin;
import models.Usuarios;

@WebFilter(urlPatterns = { "/*" })
public class FilterLoginPersistente implements Filter {
    private static final String COOKIE_LEMBRAR = "GDI_REMEMBER";

    public void init(FilterConfig fConfig) throws ServletException { }
    public void destroy() { }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Usuarios usuario = usuarioDaSessao(req);
        if (usuario == null) {
            // Public and protected paths both pass here, so token login happens before redirects.
            usuario = autenticarPorCookie(req, resp);
        }

        if (usuario != null && isPaginaLogin(req)) {
            resp.sendRedirect(req.getContextPath() + "/views/principal.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    private Usuarios usuarioDaSessao(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        return sessao == null ? null : (Usuarios) sessao.getAttribute("UsuarioLogado");
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

    private boolean isPaginaLogin(HttpServletRequest req) {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        return path == null || path.isEmpty() || "/".equals(path) || "/index.jsp".equals(path);
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
}
