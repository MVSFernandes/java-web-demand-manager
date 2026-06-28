package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOSetor;
import models.Setores;
import util.ValidacaoException;

@WebServlet("/ServletSetor")
public class ServletSetor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAOSetor daoSetor = new DAOSetor();

    public ServletSetor() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoSetor.excluirSetor(id);
                request.getSession().setAttribute("Msg", "Setor excluido com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/setores.jsp");

            } else if ("carregar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Setores setor = daoSetor.consultarSetor(id);
                request.getSession().setAttribute("Setor", setor);
                response.sendRedirect(request.getContextPath() + "/views/setores.jsp");

            } else {
                response.sendRedirect(request.getContextPath() + "/views/setores.jsp");
            }

        } catch (ValidacaoException e) {
            request.getSession().setAttribute("Msg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/views/setores.jsp");
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
            String idGerente = request.getParameter("idGerenteSetor");

            Setores setor = new Setores();
            setor.setIdSetor(id != null && !id.isEmpty() ? Integer.parseInt(id) : null);
            setor.setNomeSetor(nome);
            setor.setDescricaoSetor(descricao);
            setor.setIdGerenteSetor(idGerente != null && !idGerente.isEmpty() ? Integer.parseInt(idGerente) : null);

            String msg = setor.isNovo() ? "Setor cadastrado com sucesso!" : "Setor atualizado com sucesso!";
            daoSetor.gravarSetor(setor);
            request.getSession().setAttribute("Msg", msg);
            response.sendRedirect(request.getContextPath() + "/views/setores.jsp");

        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }
}
