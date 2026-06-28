package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import bd.ConexaoBanco;
import models.Usuarios;

public class DAOLogin {

    private Connection conexaoBanco;

    public DAOLogin() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public Usuarios verificarLogin(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email_usuario = ? AND (senha_hash_usuario = ? OR senha_hash_usuario = MD5(?))";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, email);
        stmt.setString(2, senha);
        stmt.setString(3, senha);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Usuarios usu = new Usuarios();
            usu.setIdUsuario(rs.getInt("id_usuario"));
            usu.setNomeUsuario(rs.getString("nome_usuario"));
            usu.setEmailUsuario(rs.getString("email_usuario"));
            usu.setPerfilUsuario(rs.getString("perfil_usuario"));
            usu.setIdSetorUsuario(rs.getInt("id_setor_usuario"));
            usu.setAtivoUsuario(rs.getInt("ativo_usuario"));
            return usu;
        }
        return null;
    }

    public String criarTokenPersistente(int idUsuario, int diasValidade) throws Exception {
        // Existing databases may predate persistent login, so token storage bootstraps itself.
        criarTabelaTokensSeNecessario();

        String token = gerarToken();
        String tokenHash = hashToken(token);
        LocalDateTime expiraEm = LocalDateTime.now().plusDays(diasValidade);

        String sql = "INSERT INTO login_tokens (id_usuario_token, token_hash, expira_em_token) VALUES (?, ?, ?)";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        stmt.setString(2, tokenHash);
        stmt.setTimestamp(3, Timestamp.valueOf(expiraEm));
        stmt.executeUpdate();
        return token;
    }

    public Usuarios autenticarPorToken(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) return null;
        criarTabelaTokensSeNecessario();
        limparTokensExpirados();

        String sql = "SELECT u.* FROM login_tokens lt " +
            "JOIN usuarios u ON lt.id_usuario_token = u.id_usuario " +
            "WHERE lt.token_hash = ? AND lt.expira_em_token > NOW() AND u.ativo_usuario = 1";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, hashToken(token));
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            atualizarUsoToken(token);
            return mapearUsuario(rs);
        }
        return null;
    }

    public void removerToken(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) return;
        criarTabelaTokensSeNecessario();
        String sql = "DELETE FROM login_tokens WHERE token_hash = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, hashToken(token));
        stmt.executeUpdate();
    }

    public void removerTokensDoUsuario(int idUsuario) throws Exception {
        criarTabelaTokensSeNecessario();
        String sql = "DELETE FROM login_tokens WHERE id_usuario_token = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        stmt.executeUpdate();
    }

    private void atualizarUsoToken(String token) throws Exception {
        String sql = "UPDATE login_tokens SET ultimo_uso_token = NOW() WHERE token_hash = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, hashToken(token));
        stmt.executeUpdate();
    }

    private void limparTokensExpirados() throws Exception {
        String sql = "DELETE FROM login_tokens WHERE expira_em_token <= NOW()";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.executeUpdate();
    }

    private void criarTabelaTokensSeNecessario() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS login_tokens (" +
            "id_token INT AUTO_INCREMENT PRIMARY KEY, " +
            "id_usuario_token INT NOT NULL, " +
            "token_hash VARCHAR(64) NOT NULL UNIQUE, " +
            "criado_em_token TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "ultimo_uso_token TIMESTAMP NULL, " +
            "expira_em_token TIMESTAMP NOT NULL, " +
            "INDEX idx_login_tokens_usuario (id_usuario_token), " +
            "CONSTRAINT fk_login_tokens_usuario FOREIGN KEY (id_usuario_token) " +
            "REFERENCES usuarios(id_usuario) ON DELETE CASCADE" +
            ")";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.executeUpdate();
    }

    private String gerarToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) throws Exception {
        // Hashing keeps the database from storing reusable cookie tokens.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(token.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private Usuarios mapearUsuario(ResultSet rs) throws SQLException {
        Usuarios usu = new Usuarios();
        usu.setIdUsuario(rs.getInt("id_usuario"));
        usu.setNomeUsuario(rs.getString("nome_usuario"));
        usu.setEmailUsuario(rs.getString("email_usuario"));
        usu.setPerfilUsuario(rs.getString("perfil_usuario"));
        usu.setIdSetorUsuario(rs.getInt("id_setor_usuario"));
        usu.setAtivoUsuario(rs.getInt("ativo_usuario"));
        return usu;
    }
}
