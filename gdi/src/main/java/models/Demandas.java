package models;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class Demandas {

    private Integer idDemanda;
    private String tituloDemanda;
    private String descricaoDemanda;
    private int prioridadeDemanda;
    private Timestamp slaDataLimiteDemanda;
    private Timestamp abertaEmDemanda;
    private Timestamp concluidaEmDemanda;
    private Integer idSolicitanteDemanda;
    private Integer idSetorDestinoDemanda;
    private Integer idCategoriaDemanda;
    private Integer idStatusDemanda;

    
    private String nomeSolicitante;
    private String nomeSetorDestino;
    private String nomeCategoria;
    private String nomeStatus;

    public Demandas() { }

    public boolean isNova() {
        return this.idDemanda == null;
    }

    public String getPrioridadeTexto() {
        switch (this.prioridadeDemanda) {
            case 1: return "Alta";
            case 2: return "Média";
            case 3: return "Baixa";
            default: return "Média";
        }
    }

    public String getPrioridadeCss() {
        switch (this.prioridadeDemanda) {
            case 1: return "alta";
            case 2: return "media";
            case 3: return "baixa";
            default: return "media";
        }
    }

    public String getSlaStatus() {
        LocalDateTime limite = getLimiteSlaEfetivo();
        if (limite == null) return "sem-sla";
        if (concluidaEmDemanda != null || isStatusConcluido()) return "ok";

        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(limite)) return "vencido";

        long horas = Duration.between(agora, limite).toHours();
        if (horas <= 24) return "urgente";
        if (horas <= 72) return "atencao";
        return "ok";
    }

    public String getSlaTexto() {
        LocalDateTime limite = getLimiteSlaEfetivo();
        if (limite == null) return "Sem SLA";

        LocalDateTime agora = LocalDateTime.now();
        long horas = Duration.between(agora, limite).toHours();
        if (horas < 0) {
            long dias = Math.max(1, Math.abs(horas) / 24);
            return "-" + dias + "d";
        }
        if (horas < 24) return horas + "h";
        return Math.max(1, horas / 24) + "d";
    }

    public String getSlaIcone() {
        String status = getSlaStatus();
        if ("vencido".equals(status)) return "alarm-clock-off";
        if ("urgente".equals(status) || "atencao".equals(status)) return "alarm-clock";
        return "clock";
    }

    private LocalDateTime getLimiteSlaEfetivo() {
        if (slaDataLimiteDemanda != null) {
            return slaDataLimiteDemanda.toLocalDateTime();
        }

        LocalDateTime inicio = abertaEmDemanda != null
            ? abertaEmDemanda.toLocalDateTime()
            : LocalDateTime.now();
        return inicio.plusDays(getDiasSlaPadrao());
    }

    private int getDiasSlaPadrao() {
        switch (this.prioridadeDemanda) {
            case 1: return 2;
            case 3: return 10;
            case 2:
            default: return 5;
        }
    }

    private boolean isStatusConcluido() {
        if (nomeStatus == null) return false;
        String status = nomeStatus.toLowerCase();
        return status.contains("conclu") || status.contains("cancel");
    }

    public Integer getIdDemanda() { return idDemanda; }
    public void setIdDemanda(Integer idDemanda) { this.idDemanda = idDemanda; }

    public String getTituloDemanda() { return tituloDemanda; }
    public void setTituloDemanda(String tituloDemanda) { this.tituloDemanda = tituloDemanda; }

    public String getDescricaoDemanda() { return descricaoDemanda; }
    public void setDescricaoDemanda(String descricaoDemanda) { this.descricaoDemanda = descricaoDemanda; }

    public int getPrioridadeDemanda() { return prioridadeDemanda; }
    public void setPrioridadeDemanda(int prioridadeDemanda) { this.prioridadeDemanda = prioridadeDemanda; }

    public Timestamp getSlaDataLimiteDemanda() { return slaDataLimiteDemanda; }
    public void setSlaDataLimiteDemanda(Timestamp slaDataLimiteDemanda) { this.slaDataLimiteDemanda = slaDataLimiteDemanda; }

    public Timestamp getAbertaEmDemanda() { return abertaEmDemanda; }
    public void setAbertaEmDemanda(Timestamp abertaEmDemanda) { this.abertaEmDemanda = abertaEmDemanda; }

    public Timestamp getConcluidaEmDemanda() { return concluidaEmDemanda; }
    public void setConcluidaEmDemanda(Timestamp concluidaEmDemanda) { this.concluidaEmDemanda = concluidaEmDemanda; }

    public Integer getIdSolicitanteDemanda() { return idSolicitanteDemanda; }
    public void setIdSolicitanteDemanda(Integer idSolicitanteDemanda) { this.idSolicitanteDemanda = idSolicitanteDemanda; }

    public Integer getIdSetorDestinoDemanda() { return idSetorDestinoDemanda; }
    public void setIdSetorDestinoDemanda(Integer idSetorDestinoDemanda) { this.idSetorDestinoDemanda = idSetorDestinoDemanda; }

    public Integer getIdCategoriaDemanda() { return idCategoriaDemanda; }
    public void setIdCategoriaDemanda(Integer idCategoriaDemanda) { this.idCategoriaDemanda = idCategoriaDemanda; }

    public Integer getIdStatusDemanda() { return idStatusDemanda; }
    public void setIdStatusDemanda(Integer idStatusDemanda) { this.idStatusDemanda = idStatusDemanda; }

    public String getNomeSolicitante() { return nomeSolicitante; }
    public void setNomeSolicitante(String nomeSolicitante) { this.nomeSolicitante = nomeSolicitante; }

    public String getNomeSetorDestino() { return nomeSetorDestino; }
    public void setNomeSetorDestino(String nomeSetorDestino) { this.nomeSetorDestino = nomeSetorDestino; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    public String getNomeStatus() { return nomeStatus; }
    public void setNomeStatus(String nomeStatus) { this.nomeStatus = nomeStatus; }
}
