package business.SubHorarios;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class TurnoTP extends Turno {

    private int limite;

    public TurnoTP(int numero, DayOfWeek diaSemana, LocalTime hora, int limite, String uc, int nralocados) {
        super(numero, diaSemana, hora, uc, nralocados);
        this.limite = limite;
    }

    public TurnoTP(String[] values) {
        super(Integer.parseInt(values[0]), DayOfWeek.of(Integer.parseInt(values[1])), LocalTime.parse(values[2]), values[3]);
        this.limite = Integer.parseInt(values[6]);
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public String getTipo(){
        return("TP");
    }

    @Override
    public String toString() {
        return uc + " -> Turno TP" + super.getNumero() + ": " + super.getHora() + " - " + super.getSala();
    }
}
