package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Usuarios;

public class DAOUsuario {

    private Connection conexaoBanco;

    public DAOUsuario() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public Usuarios gravarUsuario(Usuarios user) throws Exception {
        if (user.isNovo()) {
            String sql = "INSERT INTO usuarios (nome_usuario, email_usuario, senha_hash_usuario, perfil_usuario, ativo_usuario, id_setor_usuario) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getNomeUsuario());
            stmt.setString(2, user.getEmailUsuario());
            stmt.setString(3, user.getSenhaHashUsuario());
            stmt.setString(4, user.getPerfilUsuario() == null ? "usuario" : user.getPerfilUsuario());
            stmt.setInt(5, user.getAtivoUsuario() == 0 ? 1 : user.getAtivoUsuario());
            if (user.getIdSetorUsuario() != null && user.getIdSetorUsuario() > 0) {
                stmt.setInt(6, user.getIdSetorUsuario());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setIdUsuario(rs.getInt(1));
            }
        } else {
            boolean manterSenha = user.getSenhaHashUsuario() == null || user.getSenhaHashUsuario().trim().isEmpty();
            String sql = manterSenha
                ? "UPDATE usuarios SET nome_usuario = ?, email_usuario = ?, perfil_usuario = ?, ativo_usuario = ?, id_setor_usuario = ? WHERE id_usuario = ?"
                : "UPDATE usuarios SET nome_usuario = ?, email_usuario = ?, senha_hash_usuario = ?, perfil_usuario = ?, ativo_usuario = ?, id_setor_usuario = ? WHERE id_usuario = ?";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            stmt.setString(1, user.getNomeUsuario());
            stmt.setString(2, user.getEmailUsuario());
            int index = 3;
            if (!manterSenha) stmt.setString(index++, user.getSenhaHashUsuario());
            stmt.setString(index++, user.getPerfilUsuario());
            stmt.setInt(index++, user.getAtivoUsuario() == 0 ? 1 : user.getAtivoUsuario());
            if (user.getIdSetorUsuario() != null && user.getIdSetorUsuario() > 0) {
                stmt.setInt(index++, user.getIdSetorUsuario());
            } else {
                stmt.setNull(index++, java.sql.Types.INTEGER);
            }
            stmt.setInt(index, user.getIdUsuario());
            stmt.executeUpdate();
        }
        return this.consultarUsuario(user.getIdUsuario());
    }

    public void alterarAtivoUsuario(int id, int ativo) throws Exception {
        String sql = "UPDATE usuarios SET ativo_usuario = ? WHERE id_usuario = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, ativo);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public void excluirUsuario(int id) throws Exception {
        new DAOIntegridade().validarExclusaoUsuario(id);
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Usuarios consultarUsuario(int id) throws Exception {
        Usuarios user = new Usuarios();
        String sql = "SELECT u.*, s.nome_setor FROM usuarios u LEFT JOIN setores s ON u.id_setor_usuario = s.id_setor WHERE u.id_usuario = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            user.setIdUsuario(rs.getInt("id_usuario"));
            user.setNomeUsuario(rs.getString("nome_usuario"));
            user.setEmailUsuario(rs.getString("email_usuario"));
            user.setSenhaHashUsuario(rs.getString("senha_hash_usuario"));
            user.setPerfilUsuario(rs.getString("perfil_usuario"));
            user.setAtivoUsuario(rs.getInt("ativo_usuario"));
            user.setIdSetorUsuario(rs.getInt("id_setor_usuario"));
            user.setNomeSetor(rs.getString("nome_setor"));
        }
        return user;
    }

    public List<Usuarios> listarTodosUsuarios() throws Exception {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT u.*, s.nome_setor FROM usuarios u LEFT JOIN setores s ON u.id_setor_usuario = s.id_setor ORDER BY u.nome_usuario";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuarios user = new Usuarios(
                rs.getInt("id_usuario"),
                rs.getString("nome_usuario"),
                rs.getString("email_usuario"),
                rs.getString("senha_hash_usuario"),
                rs.getString("perfil_usuario"),
                rs.getInt("ativo_usuario"),
                rs.getInt("id_setor_usuario")
            );
            user.setNomeSetor(rs.getString("nome_setor"));
            lista.add(user);
        }
        return lista;
    }

    public List<Usuarios> listarGerentes() throws Exception {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT u.*, s.nome_setor FROM usuarios u LEFT JOIN setores s ON u.id_setor_usuario = s.id_setor WHERE u.perfil_usuario = 'gerente' AND u.ativo_usuario = 1 ORDER BY u.nome_usuario";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuarios user = new Usuarios(
                rs.getInt("id_usuario"),
                rs.getString("nome_usuario"),
                rs.getString("email_usuario"),
                rs.getString("senha_hash_usuario"),
                rs.getString("perfil_usuario"),
                rs.getInt("ativo_usuario"),
                rs.getInt("id_setor_usuario")
            );
            user.setNomeSetor(rs.getString("nome_setor"));
            lista.add(user);
        }
        return lista;
    }

    public List<Usuarios> pesquisarUsuario(String nome) throws Exception {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT u.*, s.nome_setor FROM usuarios u LEFT JOIN setores s ON u.id_setor_usuario = s.id_setor WHERE u.nome_usuario LIKE ? ORDER BY u.nome_usuario";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, "%" + nome + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuarios user = new Usuarios(
                rs.getInt("id_usuario"),
                rs.getString("nome_usuario"),
                rs.getString("email_usuario"),
                rs.getString("senha_hash_usuario"),
                rs.getString("perfil_usuario"),
                rs.getInt("ativo_usuario"),
                rs.getInt("id_setor_usuario")
            );
            user.setNomeSetor(rs.getString("nome_setor"));
            lista.add(user);
        }
        return lista;
    }
}
