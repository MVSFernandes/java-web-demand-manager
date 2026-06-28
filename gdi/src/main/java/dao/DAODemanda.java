package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Demandas;

public class DAODemanda {

    private Connection conexaoBanco;

    public DAODemanda() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public Demandas gravarDemanda(Demandas dem) throws Exception {
        int idUsuarioHistorico = dem.getIdSolicitanteDemanda() == null ? 1 : dem.getIdSolicitanteDemanda();
        return gravarDemanda(dem, idUsuarioHistorico);
    }

    public Demandas gravarDemanda(Demandas dem, int idUsuarioHistorico) throws Exception {
        Integer statusAnterior = dem.isNova() ? null : consultarStatusAtual(dem.getIdDemanda());

        if (dem.isNova()) {
            String sql = "INSERT INTO demandas (titulo_demanda, descricao_demanda, prioridade_demanda, "
                    + "sla_data_limite_demanda, id_solicitante_demanda, id_setor_destino_demanda, "
                    + "id_categoria_demanda, id_status_demanda) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, dem.getTituloDemanda());
            stmt.setString(2, dem.getDescricaoDemanda());
            stmt.setInt(3, dem.getPrioridadeDemanda());
            stmt.setTimestamp(4, dem.getSlaDataLimiteDemanda());
            stmt.setInt(5, dem.getIdSolicitanteDemanda());
            if (dem.getIdSetorDestinoDemanda() != null && dem.getIdSetorDestinoDemanda() > 0) {
                stmt.setInt(6, dem.getIdSetorDestinoDemanda());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            if (dem.getIdCategoriaDemanda() != null && dem.getIdCategoriaDemanda() > 0) {
                stmt.setInt(7, dem.getIdCategoriaDemanda());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            stmt.setInt(8, dem.getIdStatusDemanda());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) dem.setIdDemanda(rs.getInt(1));
            registrarHistoricoStatus(dem.getIdDemanda(), idUsuarioHistorico, null, dem.getIdStatusDemanda(), "Demanda criada");
        } else {
            String sql = "UPDATE demandas SET titulo_demanda = ?, descricao_demanda = ?, prioridade_demanda = ?, "
                    + "sla_data_limite_demanda = ?, id_setor_destino_demanda = ?, id_categoria_demanda = ?, "
                    + "id_status_demanda = ? WHERE id_demanda = ?";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            stmt.setString(1, dem.getTituloDemanda());
            stmt.setString(2, dem.getDescricaoDemanda());
            stmt.setInt(3, dem.getPrioridadeDemanda());
            stmt.setTimestamp(4, dem.getSlaDataLimiteDemanda());
            if (dem.getIdSetorDestinoDemanda() != null && dem.getIdSetorDestinoDemanda() > 0) {
                stmt.setInt(5, dem.getIdSetorDestinoDemanda());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            if (dem.getIdCategoriaDemanda() != null && dem.getIdCategoriaDemanda() > 0) {
                stmt.setInt(6, dem.getIdCategoriaDemanda());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setInt(7, dem.getIdStatusDemanda());
            stmt.setInt(8, dem.getIdDemanda());
            stmt.executeUpdate();
            if (statusAnterior == null || !statusAnterior.equals(dem.getIdStatusDemanda())) {
                registrarHistoricoStatus(dem.getIdDemanda(), idUsuarioHistorico, statusAnterior, dem.getIdStatusDemanda(), "Status atualizado");
            }
        }
        return this.consultarDemanda(dem.getIdDemanda());
    }

    public void excluirDemanda(int id) throws Exception {
        new DAOIntegridade().validarExclusaoDemanda(id);
        String sql = "DELETE FROM demandas WHERE id_demanda = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Demandas consultarDemanda(int id) throws Exception {
        Demandas dem = new Demandas();
        String sql = "SELECT d.*, u.nome_usuario, s.nome_setor, c.nome_categoria, st.nome_status "
                   + "FROM demandas d "
                   + "LEFT JOIN usuarios u ON d.id_solicitante_demanda = u.id_usuario "
                   + "LEFT JOIN setores s ON d.id_setor_destino_demanda = s.id_setor "
                   + "LEFT JOIN categorias c ON d.id_categoria_demanda = c.id_categoria "
                   + "LEFT JOIN status st ON d.id_status_demanda = st.id_status "
                   + "WHERE d.id_demanda = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            mapearDemanda(dem, rs);
        }
        return dem;
    }

    public List<Demandas> listarTodasDemandas() throws Exception {
        List<Demandas> lista = new ArrayList<>();
        String sql = "SELECT d.*, u.nome_usuario, s.nome_setor, c.nome_categoria, st.nome_status "
                   + "FROM demandas d "
                   + "LEFT JOIN usuarios u ON d.id_solicitante_demanda = u.id_usuario "
                   + "LEFT JOIN setores s ON d.id_setor_destino_demanda = s.id_setor "
                   + "LEFT JOIN categorias c ON d.id_categoria_demanda = c.id_categoria "
                   + "LEFT JOIN status st ON d.id_status_demanda = st.id_status "
                   + "ORDER BY d.aberta_em_demanda DESC";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Demandas dem = new Demandas();
            mapearDemanda(dem, rs);
            lista.add(dem);
        }
        return lista;
    }

    public List<Demandas> listarDemandasPorSolicitante(int idUsuario) throws Exception {
        List<Demandas> lista = new ArrayList<>();
        String sql = "SELECT d.*, u.nome_usuario, s.nome_setor, c.nome_categoria, st.nome_status "
                   + "FROM demandas d "
                   + "LEFT JOIN usuarios u ON d.id_solicitante_demanda = u.id_usuario "
                   + "LEFT JOIN setores s ON d.id_setor_destino_demanda = s.id_setor "
                   + "LEFT JOIN categorias c ON d.id_categoria_demanda = c.id_categoria "
                   + "LEFT JOIN status st ON d.id_status_demanda = st.id_status "
                   + "WHERE d.id_solicitante_demanda = ? "
                   + "ORDER BY d.aberta_em_demanda DESC";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Demandas dem = new Demandas();
            mapearDemanda(dem, rs);
            lista.add(dem);
        }
        return lista;
    }

    public List<Demandas> pesquisarDemanda(String titulo) throws Exception {
        List<Demandas> lista = new ArrayList<>();
        String sql = "SELECT d.*, u.nome_usuario, s.nome_setor, c.nome_categoria, st.nome_status "
                   + "FROM demandas d "
                   + "LEFT JOIN usuarios u ON d.id_solicitante_demanda = u.id_usuario "
                   + "LEFT JOIN setores s ON d.id_setor_destino_demanda = s.id_setor "
                   + "LEFT JOIN categorias c ON d.id_categoria_demanda = c.id_categoria "
                   + "LEFT JOIN status st ON d.id_status_demanda = st.id_status "
                   + "WHERE d.titulo_demanda LIKE ? "
                   + "ORDER BY d.aberta_em_demanda DESC";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, "%" + titulo + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Demandas dem = new Demandas();
            mapearDemanda(dem, rs);
            lista.add(dem);
        }
        return lista;
    }

    private void mapearDemanda(Demandas dem, ResultSet rs) throws Exception {
        dem.setIdDemanda(rs.getInt("id_demanda"));
        dem.setTituloDemanda(rs.getString("titulo_demanda"));
        dem.setDescricaoDemanda(rs.getString("descricao_demanda"));
        dem.setPrioridadeDemanda(rs.getInt("prioridade_demanda"));
        dem.setSlaDataLimiteDemanda(rs.getTimestamp("sla_data_limite_demanda"));
        dem.setAbertaEmDemanda(rs.getTimestamp("aberta_em_demanda"));
        dem.setConcluidaEmDemanda(rs.getTimestamp("concluida_em_demanda"));
        dem.setIdSolicitanteDemanda(rs.getInt("id_solicitante_demanda"));
        dem.setIdSetorDestinoDemanda(rs.getInt("id_setor_destino_demanda"));
        dem.setIdCategoriaDemanda(rs.getInt("id_categoria_demanda"));
        dem.setIdStatusDemanda(rs.getInt("id_status_demanda"));
        dem.setNomeSolicitante(rs.getString("nome_usuario"));
        dem.setNomeSetorDestino(rs.getString("nome_setor"));
        dem.setNomeCategoria(rs.getString("nome_categoria"));
        dem.setNomeStatus(rs.getString("nome_status"));
    }

    private Integer consultarStatusAtual(Integer idDemanda) throws Exception {
        if (idDemanda == null) return null;
        String sql = "SELECT id_status_demanda FROM demandas WHERE id_demanda = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idDemanda);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("id_status_demanda") : null;
    }

    private void registrarHistoricoStatus(Integer idDemanda, int idUsuario, Integer statusAnterior,
            Integer statusNovo, String observacao) throws Exception {
        if (idDemanda == null || statusNovo == null) return;
        criarTabelaHistoricoStatusSeNecessario();

        String sql = "INSERT INTO historico_status " +
            "(id_demanda_hist, id_usuario_hist, status_anterior, status_novo, observacao_hist) " +
            "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, idDemanda);
        stmt.setInt(2, idUsuario);
        if (statusAnterior == null) stmt.setNull(3, Types.INTEGER);
        else stmt.setInt(3, statusAnterior);
        stmt.setInt(4, statusNovo);
        stmt.setString(5, observacao);
        stmt.executeUpdate();
    }

    private void criarTabelaHistoricoStatusSeNecessario() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS historico_status (" +
            "id_historico INT AUTO_INCREMENT PRIMARY KEY, " +
            "id_demanda_hist INT NOT NULL, " +
            "id_usuario_hist INT NOT NULL, " +
            "status_anterior INT NULL, " +
            "status_novo INT NOT NULL, " +
            "observacao_hist VARCHAR(500) NULL, " +
            "alterado_em_hist TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "INDEX idx_hist_demanda (id_demanda_hist), " +
            "INDEX idx_hist_usuario (id_usuario_hist), " +
            "CONSTRAINT fk_hist_demanda FOREIGN KEY (id_demanda_hist) REFERENCES demandas(id_demanda) ON DELETE CASCADE, " +
            "CONSTRAINT fk_hist_usuario FOREIGN KEY (id_usuario_hist) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT, " +
            "CONSTRAINT fk_hist_status_ant FOREIGN KEY (status_anterior) REFERENCES status(id_status) ON DELETE SET NULL, " +
            "CONSTRAINT fk_hist_status_novo FOREIGN KEY (status_novo) REFERENCES status(id_status) ON DELETE RESTRICT" +
            ")";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.executeUpdate();
    }
}
