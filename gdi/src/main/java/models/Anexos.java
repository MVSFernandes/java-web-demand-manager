package models;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class Anexos {

    private Integer idAnexo;
    private Integer idDemandaAnexo;
    private Integer idUsuarioUploadAnexo;
    private String  nomeArquivoAnexo;
    private String  tipoMimeAnexo;
    private long    tamanhoBytes;
    private String  urlArmazenamentoAnexo;
    private Timestamp criadoEmAnexo;

    
    private String nomeUsuarioUpload;

    public Anexos() { }

    public String getTamanhoFormatado() {
        if (tamanhoBytes < 1024) return tamanhoBytes + " B";
        if (tamanhoBytes < 1024 * 1024) return String.format("%.1f KB", tamanhoBytes / 1024.0);
        return String.format("%.1f MB", tamanhoBytes / (1024.0 * 1024));
    }

    public boolean isImagem() {
        if (tipoMimeAnexo == null) return false;
        return tipoMimeAnexo.startsWith("image/");
    }

    public String getIcone() {
        if (tipoMimeAnexo == null) return "file";
        if (tipoMimeAnexo.startsWith("image/"))       return "image";
        if (tipoMimeAnexo.equals("application/pdf"))  return "file-text";
        if (tipoMimeAnexo.contains("word"))            return "file-text";
        if (tipoMimeAnexo.contains("excel") || tipoMimeAnexo.contains("spreadsheet"))
                                                       return "file-spreadsheet";
        if (tipoMimeAnexo.contains("zip") || tipoMimeAnexo.contains("compressed"))
                                                       return "archive";
        return "file";
    }

    public String getTipoArquivoTexto() {
        if (tipoMimeAnexo == null || tipoMimeAnexo.isEmpty()) return "Arquivo";
        if (tipoMimeAnexo.startsWith("image/")) return "Imagem";
        if (tipoMimeAnexo.equals("application/pdf")) return "PDF";
        if (tipoMimeAnexo.contains("word")) return "Documento";
        if (tipoMimeAnexo.contains("excel") || tipoMimeAnexo.contains("spreadsheet")) return "Planilha";
        if (tipoMimeAnexo.contains("zip") || tipoMimeAnexo.contains("compressed")) return "Compactado";
        if (tipoMimeAnexo.startsWith("text/")) return "Texto";
        return "Arquivo";
    }

    public String getTipoClasse() {
        if (tipoMimeAnexo == null) return "doc";
        if (tipoMimeAnexo.startsWith("image/")) return "img";
        if (tipoMimeAnexo.equals("application/pdf")) return "pdf";
        if (tipoMimeAnexo.contains("zip") || tipoMimeAnexo.contains("compressed")) return "zip";
        if (tipoMimeAnexo.contains("excel") || tipoMimeAnexo.contains("spreadsheet")) return "xls";
        return "doc";
    }

    public boolean isPreviewDisponivel() {
        if (tipoMimeAnexo == null) return false;
        return tipoMimeAnexo.startsWith("image/") || tipoMimeAnexo.equals("application/pdf");
    }

    public String getPreviewTipo() {
        if (tipoMimeAnexo != null && tipoMimeAnexo.startsWith("image/")) return "image";
        if (tipoMimeAnexo != null && tipoMimeAnexo.equals("application/pdf")) return "pdf";
        return "download";
    }

    public String getCriadoEmRelativo() {
        if (criadoEmAnexo == null) return "agora";

        long minutos = Math.max(0, Duration.between(criadoEmAnexo.toLocalDateTime(), LocalDateTime.now()).toMinutes());
        if (minutos < 1) return "agora";
        if (minutos < 60) return "ha " + minutos + " min";

        long horas = minutos / 60;
        if (horas < 24) return "ha " + horas + " h";

        long dias = horas / 24;
        return "ha " + dias + " d";
    }

    public Integer getIdAnexo() { return idAnexo; }
    public void setIdAnexo(Integer idAnexo) { this.idAnexo = idAnexo; }

    public Integer getIdDemandaAnexo() { return idDemandaAnexo; }
    public void setIdDemandaAnexo(Integer idDemandaAnexo) { this.idDemandaAnexo = idDemandaAnexo; }

    public Integer getIdUsuarioUploadAnexo() { return idUsuarioUploadAnexo; }
    public void setIdUsuarioUploadAnexo(Integer idUsuarioUploadAnexo) { this.idUsuarioUploadAnexo = idUsuarioUploadAnexo; }

    public String getNomeArquivoAnexo() { return nomeArquivoAnexo; }
    public void setNomeArquivoAnexo(String nomeArquivoAnexo) { this.nomeArquivoAnexo = nomeArquivoAnexo; }

    public String getTipoMimeAnexo() { return tipoMimeAnexo; }
    public void setTipoMimeAnexo(String tipoMimeAnexo) { this.tipoMimeAnexo = tipoMimeAnexo; }

    public long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }

    public String getUrlArmazenamentoAnexo() { return urlArmazenamentoAnexo; }
    public void setUrlArmazenamentoAnexo(String urlArmazenamentoAnexo) { this.urlArmazenamentoAnexo = urlArmazenamentoAnexo; }

    public Timestamp getCriadoEmAnexo() { return criadoEmAnexo; }
    public void setCriadoEmAnexo(Timestamp criadoEmAnexo) { this.criadoEmAnexo = criadoEmAnexo; }

    public String getNomeUsuarioUpload() { return nomeUsuarioUpload; }
    public void setNomeUsuarioUpload(String nomeUsuarioUpload) { this.nomeUsuarioUpload = nomeUsuarioUpload; }
}
