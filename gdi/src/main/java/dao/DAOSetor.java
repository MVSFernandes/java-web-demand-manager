package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Setores;

public class DAOSetor {

    private Connection conexaoBanco;
    private Boolean gerenteDisponivel;

    public DAOSetor() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public Setores gravarSetor(Setores setor) throws Exception {
        if (colunaGerenteDisponivel()) {
            gravarSetorComGerente(setor);
        } else {
            gravarSetorBase(setor);
        }
        return this.consultarSetor(setor.getIdSetor());
    }

    private void gravarSetorBase(Setores setor) throws Exception {
        if (setor.isNovo()) {
            String sql = "INSERT INTO setores (nome_setor, descricao_setor) VALUES (?, ?)";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, setor.getNomeSetor());
            stmt.setString(2, setor.getDescricaoSetor());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) setor.setIdSetor(rs.getInt(1));
        } else {
            String sql = "UPDATE setores SET nome_setor = ?, descricao_setor = ? WHERE id_setor = ?";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            stmt.setString(1, setor.getNomeSetor());
            stmt.setString(2, setor.getDescricaoSetor());
            stmt.setInt(3, setor.getIdSetor());
            stmt.executeUpdate();
        }
    }

    private void gravarSetorComGerente(Setores setor) throws Exception {
        if (setor.isNovo()) {
            String sql = "INSERT INTO setores (nome_setor, descricao_setor, id_gerente_setor) VALUES (?, ?, ?)";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, setor.getNomeSetor());
            stmt.setString(2, setor.getDescricaoSetor());
            setNullableInt(stmt, 3, setor.getIdGerenteSetor());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) setor.setIdSetor(rs.getInt(1));
        } else {
            String sql = "UPDATE setores SET nome_setor = ?, descricao_setor = ?, id_gerente_setor = ? WHERE id_setor = ?";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            stmt.setString(1, setor.getNomeSetor());
            stmt.setString(2, setor.getDescricaoSetor());
            setNullableInt(stmt, 3, setor.getIdGerenteSetor());
            stmt.setInt(4, setor.getIdSetor());
            stmt.executeUpdate();
        }
    }

    public void excluirSetor(int id) throws Exception {
        new DAOIntegridade().validarExclusaoSetor(id);
        String sql = "DELETE FROM setores WHERE id_setor = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Setores consultarSetor(int id) throws Exception {
        String sql = sqlSetores("WHERE s.id_setor = ?");
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return mapearSetor(rs);
        return new Setores();
    }

    public List<Setores> listarTodosSetores() throws Exception {
        List<Setores> lista = new ArrayList<>();
        String sql = sqlSetores("ORDER BY s.nome_setor");
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) lista.add(mapearSetor(rs));
        return lista;
    }

    public List<Setores> pesquisarSetor(String nome) throws Exception {
        List<Setores> lista = new ArrayList<>();
        String sql = sqlSetores("WHERE s.nome_setor LIKE ? ORDER BY s.nome_setor");
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, "%" + nome + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) lista.add(mapearSetor(rs));
        return lista;
    }

    private String sqlSetores(String complemento) throws Exception {
        if (colunaGerenteDisponivel()) {
            return "SELECT s.*, u.nome_usuario AS nome_gerente " +
                   "FROM setores s " +
                   "LEFT JOIN usuarios u ON s.id_gerente_setor = u.id_usuario " +
                   complemento;
        }
        return "SELECT s.* FROM setores s " + complemento;
    }

    private Setores mapearSetor(ResultSet rs) throws Exception {
        Setores setor = new Setores(
            rs.getInt("id_setor"),
            rs.getString("nome_setor"),
            rs.getString("descricao_setor")
        );
        if (colunaGerenteDisponivel()) {
            int idGerente = rs.getInt("id_gerente_setor");
            setor.setIdGerenteSetor(rs.wasNull() ? null : idGerente);
            setor.setNomeGerente(rs.getString("nome_gerente"));
        }
        return setor;
    }

    private boolean colunaGerenteDisponivel() throws Exception {
        if (gerenteDisponivel != null) return gerenteDisponivel.booleanValue();
        DatabaseMetaData meta = conexaoBanco.getMetaData();
        ResultSet rs = meta.getColumns(null, null, "setores", "id_gerente_setor");
        gerenteDisponivel = rs.next();
        return gerenteDisponivel.booleanValue();
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws Exception {
        if (value != null && value > 0) stmt.setInt(index, value);
        else stmt.setNull(index, Types.INTEGER);
    }
}
