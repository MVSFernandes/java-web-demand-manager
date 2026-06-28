package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBanco;
import models.Categorias;

public class DAOCategoria {

    private Connection conexaoBanco;

    public DAOCategoria() {
        conexaoBanco = ConexaoBanco.getConnection();
    }

    public Categorias gravarCategoria(Categorias cat) throws Exception {
        if (cat.isNovo()) {
            String sql = "INSERT INTO categorias (nome_categoria, descricao_categoria) VALUES (?, ?)";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, cat.getNomeCategoria());
            stmt.setString(2, cat.getDescricaoCategoria());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) cat.setIdCategoria(rs.getInt(1));
        } else {
            String sql = "UPDATE categorias SET nome_categoria = ?, descricao_categoria = ? WHERE id_categoria = ?";
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            stmt.setString(1, cat.getNomeCategoria());
            stmt.setString(2, cat.getDescricaoCategoria());
            stmt.setInt(3, cat.getIdCategoria());
            stmt.executeUpdate();
        }
        return this.consultarCategoria(cat.getIdCategoria());
    }

    public void excluirCategoria(int id) throws Exception {
        new DAOIntegridade().validarExclusaoCategoria(id);
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Categorias consultarCategoria(int id) throws Exception {
        Categorias cat = new Categorias();
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            cat.setIdCategoria(rs.getInt("id_categoria"));
            cat.setNomeCategoria(rs.getString("nome_categoria"));
            cat.setDescricaoCategoria(rs.getString("descricao_categoria"));
        }
        return cat;
    }

    public List<Categorias> listarTodasCategorias() throws Exception {
        List<Categorias> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nome_categoria";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            lista.add(new Categorias(
                rs.getInt("id_categoria"),
                rs.getString("nome_categoria"),
                rs.getString("descricao_categoria")
            ));
        }
        return lista;
    }

    public List<Categorias> pesquisarCategoria(String nome) throws Exception {
        List<Categorias> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE nome_categoria LIKE ? ORDER BY nome_categoria";
        PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
        stmt.setString(1, "%" + nome + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            lista.add(new Categorias(
                rs.getInt("id_categoria"),
                rs.getString("nome_categoria"),
                rs.getString("descricao_categoria")
            ));
        }
        return lista;
    }
}
