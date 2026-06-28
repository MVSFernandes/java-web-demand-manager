package models;

public class Setores {

    private Integer idSetor;
    private String nomeSetor;
    private String descricaoSetor;
    private Integer idGerenteSetor;
    private String nomeGerente;

    public Setores() { }

    public Setores(Integer idSetor, String nomeSetor, String descricaoSetor) {
        this.idSetor = idSetor;
        this.nomeSetor = nomeSetor;
        this.descricaoSetor = descricaoSetor;
    }

    public boolean isNovo() {
        return this.idSetor == null;
    }

    public Integer getIdSetor() { return idSetor; }
    public void setIdSetor(Integer idSetor) { this.idSetor = idSetor; }

    public String getNomeSetor() { return nomeSetor; }
    public void setNomeSetor(String nomeSetor) { this.nomeSetor = nomeSetor; }

    public String getDescricaoSetor() { return descricaoSetor; }
    public void setDescricaoSetor(String descricaoSetor) { this.descricaoSetor = descricaoSetor; }

    public Integer getIdGerenteSetor() { return idGerenteSetor; }
    public void setIdGerenteSetor(Integer idGerenteSetor) { this.idGerenteSetor = idGerenteSetor; }

    public String getNomeGerente() { return nomeGerente; }
    public void setNomeGerente(String nomeGerente) { this.nomeGerente = nomeGerente; }
}
