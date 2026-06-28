package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOAnexo;
import models.Anexos;

@WebServlet("/ServletDownload")
public class ServletDownload extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        try {
            int id = Integer.parseInt(idParam);
            Anexos anexo = new DAOAnexo().consultarAnexo(id);

            if (anexo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo nao encontrado.");
                return;
            }

            File arquivo = new File(anexo.getUrlArmazenamentoAnexo());
            if (!arquivo.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo nao encontrado no servidor.");
                return;
            }

            String mime = anexo.getTipoMimeAnexo();
            if (mime == null) {
                mime = "application/octet-stream";
            }

            boolean preview = "preview".equals(request.getParameter("modo"))
                && (mime.startsWith("image/") || "application/pdf".equals(mime));
            String disposition = preview ? "inline" : "attachment";

            response.setContentType(mime);
            response.setHeader("Content-Disposition",
                disposition + "; filename=\"" + anexo.getNomeArquivoAnexo().replace("\"", "") + "\"");
            response.setContentLengthLong(arquivo.length());

            try (FileInputStream fis = new FileInputStream(arquivo);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesLidos;
                while ((bytesLidos = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesLidos);
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erro ao baixar arquivo: " + e.getMessage());
        }
    }
}
