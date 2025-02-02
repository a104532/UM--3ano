package business.SubUtilizadores;

import business.SubHorarios.Horario;
import business.SubHorarios.UC;

import java.util.ArrayList;
import java.util.List;

public class Aluno {

    public enum AlunoRegime {
        NAO, // não tem nenhum regime especial
        TE,
        AUM,
        DLG,
        EINT
    }

    private String numero;
    private String email;
    private String password;
    private String nome;
    private float media;
    private AlunoRegime regime;
    private boolean alocado;
    List<UC> ucs = new ArrayList<>();

    public Aluno(String numero, String nome, String email, Float media, String regime,boolean alocado) {
        this.numero = numero;
        this.nome = nome;
        this.email = email;
        this.password = numero;
        this.media = media;
        this.regime = RegimefromString(regime);
        this.alocado = alocado;
    }

    public Aluno(String[] values, int startIndex){
        this.numero = values[startIndex++];
        this.nome = values[startIndex++];
        this.email = values[startIndex++];
        this.media = Float.parseFloat(values[startIndex++]);
        if(values[startIndex].isEmpty())
            this.regime = RegimefromString("");
        else
            this.regime = RegimefromString(values[startIndex]);
        this.password = this.numero;
        this.alocado = false;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
    }

    public boolean isAlocado() {
        return alocado;
    }

    public void setAlocado(boolean alocado) {
        this.alocado = alocado;
    }

    public void addUC(UC uc){
        this.ucs.add(uc);
    }

    public String getUC(){
        return this.ucs.getFirst().getCodigo();
    }

    public List<UC> getUcs() {
        return new ArrayList<>(ucs);
    }

    public static AlunoRegime RegimefromString(String regime) {
        return switch (regime) {
            case "" -> AlunoRegime.NAO;
            case "TE" -> AlunoRegime.TE;
            case "AUM" -> AlunoRegime.AUM;
            case "DLG" -> AlunoRegime.DLG;
            case "EINT" -> AlunoRegime.EINT;
            default -> null;
        };
    }

    public  String RegimeToString() {
        return switch (this.regime) {
            case AlunoRegime.NAO -> "";
            case AlunoRegime.TE -> "TE";
            case AlunoRegime.AUM -> "AUM";
            case AlunoRegime.DLG -> "DLG";
            case AlunoRegime.EINT -> "EINT";
            default -> null;
        };
    }
}