package models;

public class Categorias {

    private Integer idCategoria;
    private String nomeCategoria;
    private String descricaoCategoria;

    public Categorias() { }

    public Categorias(Integer idCategoria, String nomeCategoria, String descricaoCategoria) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.descricaoCategoria = descricaoCategoria;
    }

    public boolean isNovo() {
        return this.idCategoria == null;
    }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    public String getDescricaoCategoria() { return descricaoCategoria; }
    public void setDescricaoCategoria(String descricaoCategoria) { this.descricaoCategoria = descricaoCategoria; }
}