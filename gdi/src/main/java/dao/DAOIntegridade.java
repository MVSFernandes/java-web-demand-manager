package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bd.ConexaoBanco;
import util.ValidacaoException;

public class DAOIntegridade {

    private Connection conexaoBanco;

    public DAOIntegridade() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public void validarExclusaoDemanda(int idDemanda) throws Exception {
        int comentarios = contar("SELECT COUNT(*) FROM comentarios WHERE id_demanda_comentario = ?", idDemanda);
        int anexos = contar("SELECT COUNT(*) FROM anexos WHERE id_demanda_anexo = ?", idDemanda);
        int historico = contarSeTabelaExiste("historico_status",
            "SELECT COUNT(*) FROM historico_status WHERE id_demanda_hist = ?", idDemanda);

        if (comentarios + anexos + historico > 0) {
            throw new ValidacaoException("ERRO: Esta demanda possui vinculos importantes (" +
                comentarios + " comentario(s), " + anexos + " anexo(s), " + historico +
                " registro(s) de historico). Preserve o historico ou remova/reatribua esses vinculos antes de excluir.");
        }
    }

    public void validarExclusaoStatus(int idStatus) throws Exception {
        int demandas = contar("SELECT COUNT(*) FROM demandas WHERE id_status_demanda = ?", idStatus);
        int historico = contarSeTabelaExiste("historico_status",
            "SELECT COUNT(*) FROM historico_status WHERE status_anterior = ? OR status_novo = ?", idStatus, idStatus);

        if (demandas + historico > 0) {
            throw new ValidacaoException("ERRO: Este status esta em uso (" + demandas +
                " demanda(s), " + historico + " registro(s) de historico). Reatribua os registros antes de excluir.");
        }
    }

    public void validarExclusaoCategoria(int idCategoria) throws Exception {
        int demandas = contar("SELECT COUNT(*) FROM demandas WHERE id_categoria_demanda = ?", idCategoria);
        if (demandas > 0) {
            throw new ValidacaoException("ERRO: Esta categoria possui " + demandas +
                " demanda(s) vinculada(s). Reatribua as demandas antes de excluir.");
        }
    }

    public void validarExclusaoSetor(int idSetor) throws Exception {
        int usuarios = contar("SELECT COUNT(*) FROM usuarios WHERE id_setor_usuario = ?", idSetor);
        int demandas = contar("SELECT COUNT(*) FROM demandas WHERE id_setor_destino_demanda = ?", idSetor);
        if (usuarios + demandas > 0) {
            throw new ValidacaoException("ERRO: Este setor possui vinculos (" + usuarios +
                " usuario(s), " + demandas + " demanda(s)). Reatribua esses registros antes de excluir.");
        }
    }

    public void validarExclusaoUsuario(int idUsuario) throws Exception {
        int demandas = contar("SELECT COUNT(*) FROM demandas WHERE id_solicitante_demanda = ?", idUsuario);
        int comentarios = contar("SELECT COUNT(*) FROM comentarios WHERE id_usuario_comentario = ?", idUsuario);
        int anexos = contar("SELECT COUNT(*) FROM anexos WHERE id_usuario_upload_anexo = ?", idUsuario);
        int setoresGerenciados = contarSeColunaExiste("setores", "id_gerente_setor",
            "SELECT COUNT(*) FROM setores WHERE id_gerente_setor = ?", idUsuario);
        int historico = contarSeTabelaExiste("historico_status",
            "SELECT COUNT(*) FROM historico_status WHERE id_usuario_hist = ?", idUsuario);

        if (demandas + comentarios + anexos + setoresGerenciados + historico > 0) {
            throw new ValidacaoException("ERRO: Este usuario possui vinculos (" + demandas +
                " demanda(s), " + comentarios + " comentario(s), " + anexos + " anexo(s), " +
                setoresGerenciados + " setor(es) gerenciado(s), " + historico +
                " registro(s) de historico). Desative o usuario ou reatribua os vinculos antes de excluir.");
        }
    }

    private int contar(String sql, Object... params) throws Exception {
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private int contarSeTabelaExiste(String tabela, String sql, Object... params) throws Exception {
        if (!tabelaExiste(tabela)) return 0;
        return contar(sql, params);
    }

    private int contarSeColunaExiste(String tabela, String coluna, String sql, Object... params) throws Exception {
        ResultSet rs = conexaoBanco.getMetaData().getColumns(null, null, tabela, coluna);
        if (!rs.next()) return 0;
        return contar(sql, params);
    }

    private boolean tabelaExiste(String tabela) throws Exception {
        ResultSet rs = conexaoBanco.getMetaData().getTables(null, null, tabela, null);
        return rs.next();
    }
}
