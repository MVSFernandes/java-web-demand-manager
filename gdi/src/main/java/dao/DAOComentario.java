package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Comentarios;

public class DAOComentario {

    private Connection conexaoBanco;

    public DAOComentario() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public void gravarComentario(Comentarios com) throws Exception {
        String sql = "INSERT INTO comentarios (id_demanda_comentario, id_usuario_comentario, mensagem_comentario) VALUES (?, ?, ?)";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, com.getIdDemandaComentario());
        stmt.setInt(2, com.getIdUsuarioComentario());
        stmt.setString(3, com.getMensagemComentario());
        stmt.executeUpdate();
    }

    public void excluirComentario(int id) throws Exception {
        String sql = "DELETE FROM comentarios WHERE id_comentario = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Comentarios consultarComentario(int id) throws Exception {
        String sql = "SELECT c.*, u.nome_usuario, d.titulo_demanda FROM comentarios c " +
                   "JOIN usuarios u ON c.id_usuario_comentario = u.id_usuario " +
                   "JOIN demandas d ON c.id_demanda_comentario = d.id_demanda " +
                   "WHERE c.id_comentario = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) return null;
        Comentarios com = new Comentarios();
        com.setIdComentario(rs.getInt("id_comentario"));
        com.setIdDemandaComentario(rs.getInt("id_demanda_comentario"));
        com.setIdUsuarioComentario(rs.getInt("id_usuario_comentario"));
        com.setMensagemComentario(rs.getString("mensagem_comentario"));
        com.setCriadoEmComentario(rs.getTimestamp("criado_em_comentario"));
        com.setNomeUsuario(rs.getString("nome_usuario"));
        com.setTituloDemanda(rs.getString("titulo_demanda"));
        return com;
    }

    public List<Comentarios> listarComentariosPorDemanda(int idDemanda) throws Exception {
        List<Comentarios> lista = new ArrayList<>();
        String sql = "SELECT c.*, u.nome_usuario FROM comentarios c "
                   + "JOIN usuarios u ON c.id_usuario_comentario = u.id_usuario "
                   + "WHERE c.id_demanda_comentario = ? "
                   + "ORDER BY c.criado_em_comentario ASC";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idDemanda);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Comentarios com = new Comentarios();
            com.setIdComentario(rs.getInt("id_comentario"));
            com.setIdDemandaComentario(rs.getInt("id_demanda_comentario"));
            com.setIdUsuarioComentario(rs.getInt("id_usuario_comentario"));
            com.setMensagemComentario(rs.getString("mensagem_comentario"));
            com.setCriadoEmComentario(rs.getTimestamp("criado_em_comentario"));
            com.setNomeUsuario(rs.getString("nome_usuario"));
            lista.add(com);
        }
        return lista;
    }

    public List<Comentarios> listarComentariosRecentes(int limite) throws Exception {
        List<Comentarios> lista = new ArrayList<>();
        String sql = "SELECT c.*, u.nome_usuario, d.titulo_demanda FROM comentarios c "
                   + "JOIN usuarios u ON c.id_usuario_comentario = u.id_usuario "
                   + "JOIN demandas d ON c.id_demanda_comentario = d.id_demanda "
                   + "ORDER BY c.criado_em_comentario DESC LIMIT ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, limite);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Comentarios com = new Comentarios();
            com.setIdComentario(rs.getInt("id_comentario"));
            com.setIdDemandaComentario(rs.getInt("id_demanda_comentario"));
            com.setIdUsuarioComentario(rs.getInt("id_usuario_comentario"));
            com.setMensagemComentario(rs.getString("mensagem_comentario"));
            com.setCriadoEmComentario(rs.getTimestamp("criado_em_comentario"));
            com.setNomeUsuario(rs.getString("nome_usuario"));
            com.setTituloDemanda(rs.getString("titulo_demanda"));
            lista.add(com);
        }
        return lista;
    }
}
