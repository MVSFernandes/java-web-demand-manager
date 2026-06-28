package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import bd.ConexaoBanco;

public class DAONotificacao {

    private static boolean tabelaVerificada = false;
    private Connection conexaoBanco;

    public DAONotificacao() throws Exception {
        conexaoBanco = ConexaoBanco.getConnection();
        garantirTabela();
    }

    private void garantirTabela() throws Exception {
        if (tabelaVerificada) {
            return;
        }

        synchronized (DAONotificacao.class) {
            if (tabelaVerificada) {
                return;
            }

            String sql = "CREATE TABLE IF NOT EXISTS notificacoes_lidas ("
                       + "id_notificacao_lida INT AUTO_INCREMENT PRIMARY KEY,"
                       + "id_usuario_notificacao INT NOT NULL,"
                       + "chave_notificacao VARCHAR(80) NOT NULL,"
                       + "lida_em_notificacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                       + "UNIQUE KEY uk_notificacao_usuario_chave (id_usuario_notificacao, chave_notificacao),"
                       + "CONSTRAINT fk_notificacoes_lidas_usuarios "
                       + "FOREIGN KEY (id_usuario_notificacao) REFERENCES usuarios(id_usuario) "
                       + "ON DELETE CASCADE"
                       + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            Statement stmt = conexaoBanco.createStatement();
            stmt.execute(sql);
            tabelaVerificada = true;
        }
    }

    public Set<String> listarChavesLidas(int idUsuario) throws Exception {
        Set<String> chaves = new HashSet<>();
        String sql = "SELECT chave_notificacao FROM notificacoes_lidas WHERE id_usuario_notificacao = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            chaves.add(rs.getString("chave_notificacao"));
        }

        return chaves;
    }

    public void marcarComoLidas(int idUsuario, Set<String> chaves) throws Exception {
        if (chaves == null || chaves.isEmpty()) {
            return;
        }

        String sql = "INSERT IGNORE INTO notificacoes_lidas "
                   + "(id_usuario_notificacao, chave_notificacao) VALUES (?, ?)";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);

        for (String chave : chaves) {
            stmt.setInt(1, idUsuario);
            stmt.setString(2, chave);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }
}
