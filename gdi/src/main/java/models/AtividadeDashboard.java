package models;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class AtividadeDashboard implements Comparable<AtividadeDashboard> {

    private String tipo;
    private String titulo;
    private String detalhe;
    private Timestamp data;
    private String autor;
    private String acao;
    private Integer idDemanda;
    private Integer idComentario;
    private Integer idAnexo;
    private String tituloDemanda;

    public AtividadeDashboard(String tipo, String titulo, String detalhe, Timestamp data) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.detalhe = detalhe;
        this.data = data;
    }

    public AtividadeDashboard(String tipo, String autor, String acao, Integer idDemanda,
            String tituloDemanda, String detalhe, Timestamp data) {
        this.tipo = tipo;
        this.autor = autor;
        this.acao = acao;
        this.idDemanda = idDemanda;
        this.tituloDemanda = tituloDemanda;
        this.detalhe = detalhe;
        this.data = data;
        this.titulo = montarTitulo();
    }

    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDetalhe() { return detalhe; }
    public Timestamp getData() { return data; }
    public String getAutor() { return autor; }
    public String getAcao() { return acao; }
    public Integer getIdDemanda() { return idDemanda; }
    public Integer getIdComentario() { return idComentario; }
    public void setIdComentario(Integer idComentario) { this.idComentario = idComentario; }
    public Integer getIdAnexo() { return idAnexo; }
    public void setIdAnexo(Integer idAnexo) { this.idAnexo = idAnexo; }
    public String getTituloDemanda() { return tituloDemanda; }

    public String getCor() {
        if ("concluida".equals(tipo)) return "var(--green)";
        if ("aberta".equals(tipo)) return "var(--amber)";
        if ("sla".equals(tipo)) return "var(--red)";
        if ("comentario".equals(tipo)) return "var(--blue)";
        if ("anexo".equals(tipo)) return "var(--amber)";
        return "var(--slate-500)";
    }

    public String getIcone() {
        if ("sla".equals(tipo)) return "alarm-clock-off";
        if ("comentario".equals(tipo)) return "message-square";
        if ("anexo".equals(tipo)) return "paperclip";
        if ("concluida".equals(tipo)) return "circle-check";
        if ("aberta".equals(tipo)) return "folder-open";
        return "activity";
    }

    public String getUrlTipo() {
        if ("anexo".equals(tipo)) return "anexo";
        if ("comentario".equals(tipo)) return "comentario";
        return "";
    }

    public Integer getUrlId() {
        if ("anexo".equals(tipo)) return idAnexo;
        if ("comentario".equals(tipo)) return idComentario;
        return null;
    }

    public String getTempoRelativo() {
        if (data == null) return "agora";

        LocalDateTime dataEvento = data.toLocalDateTime();
        LocalDateTime agora = LocalDateTime.now();
        long minutos = Math.max(0, Duration.between(dataEvento, agora).toMinutes());

        if (minutos < 1) return "agora";
        if (minutos < 60) return "ha " + minutos + " min";

        long horas = minutos / 60;
        if (horas < 24) return "ha " + horas + " h";

        long dias = horas / 24;
        if (dias < 30) return "ha " + dias + " d";

        long meses = dias / 30;
        return "ha " + meses + " mes" + (meses > 1 ? "es" : "");
    }

    @Override
    public int compareTo(AtividadeDashboard outra) {
        if (outra == null || outra.data == null) return -1;
        if (this.data == null) return 1;
        return outra.data.compareTo(this.data);
    }

    private String montarTitulo() {
        if (autor == null || autor.trim().isEmpty()) {
            return acao + " #" + idDemanda;
        }
        return autor + " " + acao + " #" + idDemanda;
    }
}
