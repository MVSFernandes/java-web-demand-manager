package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Status;

public class DAOStatus {

    private Connection conexaoBanco;

    public DAOStatus() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public Status gravarStatus(Status st) throws Exception {
        if (st.isNovo()) {
            String sql = "INSERT INTO status (nome_status, ordem_status) VALUES (?, ?)";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, st.getNomeStatus());
            stmt.setObject(2, st.getOrdemStatus());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) st.setIdStatus(rs.getInt(1));
        } else {
            String sql = "UPDATE status SET nome_status = ?, ordem_status = ? WHERE id_status = ?";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            stmt.setString(1, st.getNomeStatus());
            stmt.setObject(2, st.getOrdemStatus());
            stmt.setInt(3, st.getIdStatus());
            stmt.executeUpdate();
        }
        return this.consultarStatus(st.getIdStatus());
    }

    public void excluirStatus(int id) throws Exception {
        new DAOIntegridade().validarExclusaoStatus(id);
        String sql = "DELETE FROM status WHERE id_status = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Status consultarStatus(int id) throws Exception {
        Status st = new Status();
        String sql = "SELECT * FROM status WHERE id_status = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            st.setIdStatus(rs.getInt("id_status"));
            st.setNomeStatus(rs.getString("nome_status"));
            st.setOrdemStatus(rs.getInt("ordem_status"));
        }
        return st;
    }

    public List<Status> listarTodosStatus() throws Exception {
        List<Status> lista = new ArrayList<>();
        String sql = "SELECT * FROM status ORDER BY ordem_status, nome_status";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            lista.add(new Status(
                rs.getInt("id_status"),
                rs.getString("nome_status"),
                rs.getInt("ordem_status")
            ));
        }
        return lista;
    }
}
