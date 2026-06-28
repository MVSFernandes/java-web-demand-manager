package models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class EventoDemanda implements Comparable<EventoDemanda> {

    private String tipo;
    private String autor;
    private String titulo;
    private String detalhe;
    private Timestamp data;
    private Integer idAutor;
    private Integer idComentario;
    private Integer idAnexo;

    public EventoDemanda(String tipo, String autor, String titulo, String detalhe, Timestamp data) {
        this.tipo = tipo;
        this.autor = autor;
        this.titulo = titulo;
        this.detalhe = detalhe;
        this.data = data;
    }

    public String getTipo() { return tipo; }
    public String getAutor() { return autor; }
    public String getTitulo() { return titulo; }
    public String getDetalhe() { return detalhe; }
    public Timestamp getData() { return data; }
    public String getDataFormatada() {
        if (data == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(data);
    }
    public Integer getIdAutor() { return idAutor; }
    public void setIdAutor(Integer idAutor) { this.idAutor = idAutor; }
    public Integer getIdComentario() { return idComentario; }
    public void setIdComentario(Integer idComentario) { this.idComentario = idComentario; }
    public Integer getIdAnexo() { return idAnexo; }
    public void setIdAnexo(Integer idAnexo) { this.idAnexo = idAnexo; }

    public String getCor() {
        if ("anexo".equals(tipo)) return "var(--accent-hover)";
        if ("comentario".equals(tipo)) return "var(--blue)";
        return "var(--slate-500)";
    }

    public String getIcone() {
        if ("anexo".equals(tipo)) return "paperclip";
        if ("comentario".equals(tipo)) return "message-square";
        return "activity";
    }

    @Override
    public int compareTo(EventoDemanda outro) {
        if (outro == null || outro.data == null) return -1;
        if (this.data == null) return 1;
        return outro.data.compareTo(this.data);
    }
}
