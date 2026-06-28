package models;

import java.sql.Timestamp;

public class Comentarios {

    private Integer idComentario;
    private Integer idDemandaComentario;
    private Integer idUsuarioComentario;
    private String mensagemComentario;
    private Timestamp criadoEmComentario;

    
    private String nomeUsuario;
    private String tituloDemanda;

    public Comentarios() { }

    public Comentarios(Integer idDemandaComentario, Integer idUsuarioComentario, String mensagemComentario) {
        this.idDemandaComentario = idDemandaComentario;
        this.idUsuarioComentario = idUsuarioComentario;
        this.mensagemComentario = mensagemComentario;
    }

    public Integer getIdComentario() { return idComentario; }
    public void setIdComentario(Integer idComentario) { this.idComentario = idComentario; }

    public Integer getIdDemandaComentario() { return idDemandaComentario; }
    public void setIdDemandaComentario(Integer idDemandaComentario) { this.idDemandaComentario = idDemandaComentario; }

    public Integer getIdUsuarioComentario() { return idUsuarioComentario; }
    public void setIdUsuarioComentario(Integer idUsuarioComentario) { this.idUsuarioComentario = idUsuarioComentario; }

    public String getMensagemComentario() { return mensagemComentario; }
    public void setMensagemComentario(String mensagemComentario) { this.mensagemComentario = mensagemComentario; }

    public Timestamp getCriadoEmComentario() { return criadoEmComentario; }
    public void setCriadoEmComentario(Timestamp criadoEmComentario) { this.criadoEmComentario = criadoEmComentario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getTituloDemanda() { return tituloDemanda; }
    public void setTituloDemanda(String tituloDemanda) { this.tituloDemanda = tituloDemanda; }
}
