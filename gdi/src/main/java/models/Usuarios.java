package models;

public class Usuarios {

    private Integer idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaHashUsuario;
    private String perfilUsuario;
    private int ativoUsuario;
    private Integer idSetorUsuario;
    private String nomeSetor; 

    public Usuarios() { }

    public Usuarios(Integer idUsuario, String nomeUsuario, String emailUsuario,
                    String senhaHashUsuario, String perfilUsuario,
                    int ativoUsuario, Integer idSetorUsuario) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.senhaHashUsuario = senhaHashUsuario;
        this.perfilUsuario = perfilUsuario;
        this.ativoUsuario = ativoUsuario;
        this.idSetorUsuario = idSetorUsuario;
    }

    public Usuarios(String emailUsuario, String senhaHashUsuario) {
        this.emailUsuario = emailUsuario;
        this.senhaHashUsuario = senhaHashUsuario;
    }

    public boolean isNovo() {
        return this.idUsuario == null;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public String getSenhaHashUsuario() { return senhaHashUsuario; }
    public void setSenhaHashUsuario(String senhaHashUsuario) { this.senhaHashUsuario = senhaHashUsuario; }

    public String getPerfilUsuario() { return perfilUsuario; }
    public void setPerfilUsuario(String perfilUsuario) { this.perfilUsuario = perfilUsuario; }

    public int getAtivoUsuario() { return ativoUsuario; }
    public void setAtivoUsuario(int ativoUsuario) { this.ativoUsuario = ativoUsuario; }

    public Integer getIdSetorUsuario() { return idSetorUsuario; }
    public void setIdSetorUsuario(Integer idSetorUsuario) { this.idSetorUsuario = idSetorUsuario; }

    public String getNomeSetor() { return nomeSetor; }
    public void setNomeSetor(String nomeSetor) { this.nomeSetor = nomeSetor; }
}