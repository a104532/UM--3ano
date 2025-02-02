package business.SubHorarios;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Turno {

    private int numero;
    private DayOfWeek diaSemana;
    private LocalTime hora;
    private Sala sala;
    String uc;

    private int nralocados;

    public Turno(int numero, DayOfWeek diaSemana, LocalTime hora, String uc) {
        this.numero = numero;
        this.diaSemana = diaSemana;
        this.hora = hora;
        this.uc = uc;
        this.nralocados=0;
    }
    public Turno(int numero, DayOfWeek diaSemana, LocalTime hora, String uc, int nralocados) {
        this.numero = numero;
        this.diaSemana = diaSemana;
        this.hora = hora;
        this.uc = uc;
        this.nralocados=nralocados;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getUc() {
        return uc;
    }

    public void setUc(String uc) {
        this.uc = uc;
    }

    public int getNralocados() {
        return nralocados;
    }

    public void setNralocados(int nralocados) {
        this.nralocados = nralocados;
    }

    public void accNralocados(){
        this.nralocados+=1;
    }

    public void decNralocados(){
        this.nralocados--;
    }

    public abstract String getTipo();

    public abstract int getLimite();
}
