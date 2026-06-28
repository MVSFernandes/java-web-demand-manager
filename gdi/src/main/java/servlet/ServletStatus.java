package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOStatus;
import models.Status;
import util.ValidacaoException;

@WebServlet("/ServletStatus")
public class ServletStatus extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAOStatus daoStatus = new DAOStatus();

    public ServletStatus() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                daoStatus.excluirStatus(id);
                request.getSession().setAttribute("Msg", "Status excluido com sucesso!");
                response.sendRedirect(request.getContextPath() + "/views/status.jsp");

            } else if ("carregar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Status st = daoStatus.consultarStatus(id);
                request.getSession().setAttribute("Status", st);
                response.sendRedirect(request.getContextPath() + "/views/status.jsp");

            } else {
                response.sendRedirect(request.getContextPath() + "/views/status.jsp");
            }
        } catch (ValidacaoException e) {
            request.getSession().setAttribute("Msg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/views/status.jsp");
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
            String ordem = request.getParameter("txtOrdem");

            Status st = new Status();
            st.setIdStatus(id != null && !id.isEmpty() ? Integer.parseInt(id) : null);
            st.setNomeStatus(nome);
            st.setOrdemStatus(ordem != null && !ordem.isEmpty() ? Integer.parseInt(ordem) : null);

            String msg = st.isNovo() ? "Status cadastrado com sucesso!" : "Status atualizado com sucesso!";
            daoStatus.gravarStatus(st);
            request.getSession().setAttribute("Msg", msg);
            response.sendRedirect(request.getContextPath() + "/views/status.jsp");

        } catch (Exception e) {
            request.setAttribute("Msg", "Erro: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/Erro.jsp");
            rd.forward(request, response);
        }
    }
}
