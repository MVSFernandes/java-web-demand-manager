package servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.DAOAnexo;
import models.Anexos;
import models.Usuarios;

@WebServlet("/ServletAnexo")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      
    maxFileSize       = 10 * 1024 * 1024, 
    maxRequestSize    = 20 * 1024 * 1024  
)
public class ServletAnexo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DAOAnexo daoAnexo = new DAOAnexo();

    public ServletAnexo() {
        super();
    }

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao     = request.getParameter("acao");
        String idDemanda = request.getParameter("idDemanda");

        try {
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));

                Anexos anexo = daoAnexo.consultarAnexo(id);
                Usuarios usuarioLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
                boolean podeExcluir = anexo != null && usuarioLogado != null
                    && (usuarioLogado.getIdUsuario().equals(anexo.getIdUsuarioUploadAnexo())
                        || "admin".equals(usuarioLogado.getPerfilUsuario()));

                if (!podeExcluir) {
                    request.getSession().setAttribute("Msg", "ERRO: Voce nao tem permissao para excluir este anexo.");
                    response.sendRedirect(request.getContextPath()
                            + "/views/comentarios.jsp?idDemanda=" + idDemanda);
                    return;
                }

                if (anexo != null && anexo.getUrlArmazenamentoAnexo() != null) {
                    
                    File arquivo = new File(anexo.getUrlArmazenamentoAnexo());
                    if (arquivo.exists()) {
                        arquivo.delete();
                    }
                }
                daoAnexo.excluirAnexo(id);
                request.getSession().setAttribute("Msg", "Anexo excluído com sucesso!");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("Msg", "ERRO: Nao foi possivel excluir o anexo. " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath()
                + "/views/comentarios.jsp?idDemanda=" + idDemanda);
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idDemanda = request.getParameter("idDemanda");

        try {
            Usuarios usuLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
            int idUsuario = usuLogado != null ? usuLogado.getIdUsuario() : 1;

            
            String pastaUploads = getServletContext().getRealPath("/uploads");
            File pasta = new File(pastaUploads);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            
            Part parte = request.getPart("arquivo");
            String nomeOriginal = Paths.get(parte.getSubmittedFileName()).getFileName().toString();

            if (nomeOriginal == null || nomeOriginal.isEmpty()) {
                request.getSession().setAttribute("Msg", "ERRO: Nenhum arquivo selecionado.");
                response.sendRedirect(request.getContextPath()
                        + "/views/comentarios.jsp?idDemanda=" + idDemanda);
                return;
            }

            
            String nomeUnico = System.currentTimeMillis() + "_" + nomeOriginal;
            String caminhoCompleto = pastaUploads + File.separator + nomeUnico;

            
            parte.write(caminhoCompleto);

            
            Anexos anexo = new Anexos();
            anexo.setIdDemandaAnexo(Integer.parseInt(idDemanda));
            anexo.setIdUsuarioUploadAnexo(idUsuario);
            anexo.setNomeArquivoAnexo(nomeOriginal);
            anexo.setTipoMimeAnexo(parte.getContentType());
            anexo.setTamanhoBytes(parte.getSize());
            anexo.setUrlArmazenamentoAnexo(caminhoCompleto);

            daoAnexo.gravarAnexo(anexo);
            request.getSession().setAttribute("Msg", "Anexo enviado com sucesso!");

        } catch (Exception e) {
            request.getSession().setAttribute("Msg", "ERRO: Nao foi possivel enviar o arquivo. " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath()
                + "/views/comentarios.jsp?idDemanda=" + idDemanda);
    }
}
