package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOCategoria;
import dao.DAODemanda;
import dao.DAOSetor;
import dao.DAOUsuario;
import models.Categorias;
import models.Demandas;
import models.Setores;
import models.Usuarios;

@WebServlet("/ServletBusca")
public class ServletBusca extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ServletBusca() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String tipo = request.getParameter("tipo");
        String termo = request.getParameter("termo");
        if (termo == null) termo = "";

        try {
            StringBuilder json = new StringBuilder("[");

            if ("usuario".equals(tipo)) {
                DAOUsuario dao = new DAOUsuario();
                List<Usuarios> lista = dao.pesquisarUsuario(termo);
                for (int i = 0; i < lista.size(); i++) {
                    Usuarios u = lista.get(i);
                    if (i > 0) json.append(",");
                    json.append("{\"id\":").append(u.getIdUsuario())
                        .append(",\"nome\":\"").append(escapar(u.getNomeUsuario())).append("\"}");
                }
            } else if ("setor".equals(tipo)) {
                DAOSetor dao = new DAOSetor();
                List<Setores> lista = dao.pesquisarSetor(termo);
                for (int i = 0; i < lista.size(); i++) {
                    Setores s = lista.get(i);
                    if (i > 0) json.append(",");
                    json.append("{\"id\":").append(s.getIdSetor())
                        .append(",\"nome\":\"").append(escapar(s.getNomeSetor())).append("\"}");
                }
            } else if ("categoria".equals(tipo)) {
                DAOCategoria dao = new DAOCategoria();
                List<Categorias> lista = dao.pesquisarCategoria(termo);
                for (int i = 0; i < lista.size(); i++) {
                    Categorias c = lista.get(i);
                    if (i > 0) json.append(",");
                    json.append("{\"id\":").append(c.getIdCategoria())
                        .append(",\"nome\":\"").append(escapar(c.getNomeCategoria())).append("\"}");
                }
            } else if ("demanda".equals(tipo)) {
                DAODemanda dao = new DAODemanda();
                List<Demandas> lista = dao.pesquisarDemanda(termo);
                for (int i = 0; i < lista.size(); i++) {
                    Demandas d = lista.get(i);
                    if (i > 0) json.append(",");
                    json.append("{\"id\":").append(d.getIdDemanda())
                        .append(",\"nome\":\"").append(escapar(d.getTituloDemanda())).append("\"}");
                }
            }

            json.append("]");
            out.print(json.toString());

        } catch (Exception e) {
            out.print("[]");
        }
    }

    private String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}