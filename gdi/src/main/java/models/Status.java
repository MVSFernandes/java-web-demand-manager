package models;

public class Status {

    private Integer idStatus;
    private String nomeStatus;
    private Integer ordemStatus;

    public Status() { }

    public Status(Integer idStatus, String nomeStatus, Integer ordemStatus) {
        this.idStatus = idStatus;
        this.nomeStatus = nomeStatus;
        this.ordemStatus = ordemStatus;
    }

    public boolean isNovo() {
        return this.idStatus == null;
    }

    public Integer getIdStatus() { return idStatus; }
    public void setIdStatus(Integer idStatus) { this.idStatus = idStatus; }

    public String getNomeStatus() { return nomeStatus; }
    public void setNomeStatus(String nomeStatus) { this.nomeStatus = nomeStatus; }

    public Integer getOrdemStatus() { return ordemStatus; }
    public void setOrdemStatus(Integer ordemStatus) { this.ordemStatus = ordemStatus; }
}