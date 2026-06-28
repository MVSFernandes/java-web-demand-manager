package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Anexos;

public class DAOAnexo {

    private Connection conexaoBanco;

    public DAOAnexo() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public void gravarAnexo(Anexos anexo) throws Exception {
        String sql = "INSERT INTO anexos (id_demanda_anexo, id_usuario_upload_anexo, "
                   + "nome_arquivo_anexo, tipo_mime_anexo, tamanho_bytes_anexo, "
                   + "url_armazenamento_anexo) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, anexo.getIdDemandaAnexo());
        stmt.setInt(2, anexo.getIdUsuarioUploadAnexo());
        stmt.setString(3, anexo.getNomeArquivoAnexo());
        stmt.setString(4, anexo.getTipoMimeAnexo());
        stmt.setLong(5, anexo.getTamanhoBytes());
        stmt.setString(6, anexo.getUrlArmazenamentoAnexo());
        stmt.executeUpdate();
    }

    public void excluirAnexo(int id) throws Exception {
        String sql = "DELETE FROM anexos WHERE id_anexo = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Anexos consultarAnexo(int id) throws Exception {
        String sql = "SELECT * FROM anexos WHERE id_anexo = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapear(rs);
        }
        return null;
    }

    public List<Anexos> listarAnexosPorDemanda(int idDemanda) throws Exception {
        List<Anexos> lista = new ArrayList<>();
        String sql = "SELECT a.*, u.nome_usuario FROM anexos a "
                   + "JOIN usuarios u ON a.id_usuario_upload_anexo = u.id_usuario "
                   + "WHERE a.id_demanda_anexo = ? "
                   + "ORDER BY a.criado_em_anexo DESC";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idDemanda);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Anexos a = mapear(rs);
            a.setNomeUsuarioUpload(rs.getString("nome_usuario"));
            lista.add(a);
        }
        return lista;
    }

    public List<Anexos> listarAnexosRecentes(int limite) throws Exception {
        List<Anexos> lista = new ArrayList<>();
        String sql = "SELECT a.*, u.nome_usuario FROM anexos a "
                   + "JOIN usuarios u ON a.id_usuario_upload_anexo = u.id_usuario "
                   + "ORDER BY a.criado_em_anexo DESC LIMIT ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, limite);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Anexos a = mapear(rs);
            a.setNomeUsuarioUpload(rs.getString("nome_usuario"));
            lista.add(a);
        }
        return lista;
    }

    private Anexos mapear(ResultSet rs) throws Exception {
        Anexos a = new Anexos();
        a.setIdAnexo(rs.getInt("id_anexo"));
        a.setIdDemandaAnexo(rs.getInt("id_demanda_anexo"));
        a.setIdUsuarioUploadAnexo(rs.getInt("id_usuario_upload_anexo"));
        a.setNomeArquivoAnexo(rs.getString("nome_arquivo_anexo"));
        a.setTipoMimeAnexo(rs.getString("tipo_mime_anexo"));
        a.setTamanhoBytes(rs.getLong("tamanho_bytes_anexo"));
        a.setUrlArmazenamentoAnexo(rs.getString("url_armazenamento_anexo"));
        a.setCriadoEmAnexo(rs.getTimestamp("criado_em_anexo"));
        return a;
    }
}
