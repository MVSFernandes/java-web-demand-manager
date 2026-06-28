package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOCategoria;
import models.Categorias;
import util.ValidacaoException;

@WebServlet("/ServletCategoria")
public class ServletCategoria extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAOCategoria daoCategoria = new DAOCategoria();

    public ServletCategoria() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoCategoria.excluirCategoria(id);
                request.getSession().setAttribute("Msg", "Categoria excluida com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/categorias.jsp");

            } else if ("carregar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Categorias cat = daoCategoria.consultarCategoria(id);
                request.getSession().setAttribute("Categoria", cat);
                response.sendRedirect(request.getContextPath() + "/views/categorias.jsp");

            } else {
                response.sendRedirect(request.getContextPath() + "/views/categorias.jsp");
            }

        } catch (ValidacaoException e) {
            request.getSession().setAttribute("Msg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/views/categorias.jsp");
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
            String nome = request.getParameter("txtNome");
            String descricao = request.getParameter("txtDescricao");

            Categorias cat = new Categorias();
            cat.setIdCategoria(id != null && !id.isEmpty() ? Integer.parseInt(id) : null);
            cat.setNomeCategoria(nome);
            cat.setDescricaoCategoria(descricao);

            String msg = cat.isNovo() ? "Categoria cadastrada com sucesso!" : "Categoria atualizada com sucesso!";
            daoCategoria.gravarCategoria(cat);
            request.getSession().setAttribute("Msg", msg);
            response.sendRedirect(request.getContextPath() + "/views/categorias.jsp");

        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }
}
