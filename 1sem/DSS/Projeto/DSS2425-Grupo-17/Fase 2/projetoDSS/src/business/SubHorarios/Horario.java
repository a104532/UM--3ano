package business.SubHorarios;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class Horario {

    String id;

    private Map<DayOfWeek,List<Turno>> turnosH;

    public Horario(String id) {
        this.id = id;
        this.turnosH = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addTurno(Turno turno) { //depois basta um sort para ficar ordenado
        DayOfWeek day = turno.getDiaSemana();
        turnosH.computeIfAbsent(day, k -> new ArrayList<>()).add(turno);

    }

    public List<Turno> sortTurnosByDate(DayOfWeek day){
        List<Turno> l = turnosH.get(day);
        if(l!=null)
            l.sort(Comparator.comparing(Turno::getHora));

        return l;
    }

    public Map<DayOfWeek,List<Turno>> getTurnos(){
        return new HashMap<>(turnosH);
    }

    public List<Turno> getListTurnos(){
        return turnosH.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<DayOfWeek, List<Turno>> turnos : turnosH.entrySet()) {
            sb.append(turnos.getKey()).append(":\n");
            for (Turno turno : turnos.getValue()) {
                sb.append(turno).append("\n");
            }
        }
        return sb.toString();
    }
}
