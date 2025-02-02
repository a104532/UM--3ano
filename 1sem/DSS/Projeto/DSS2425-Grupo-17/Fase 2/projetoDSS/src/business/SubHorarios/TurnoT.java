package business.SubHorarios;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class TurnoT extends Turno {
    public TurnoT(int numero, DayOfWeek diaSemana, LocalTime hora, String uc, int nrAlocados) {
        super(numero,diaSemana, hora,uc, nrAlocados);
    }

    public TurnoT(String[] values) {
        super(Integer.parseInt(values[0]), DayOfWeek.of(Integer.parseInt(values[1])), LocalTime.parse(values[2]), values[3]);
    }

    public String getTipo(){
        return("T");
    }

    public int getLimite(){
        return super.getSala().getCapacidade();
    }

    @Override
    public String toString() {
        return uc + " -> Turno T" + super.getNumero() + ": " + super.getHora() + " - " + super.getSala();
    }
}
