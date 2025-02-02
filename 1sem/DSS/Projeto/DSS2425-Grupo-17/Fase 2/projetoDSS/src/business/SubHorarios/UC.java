package business.SubHorarios;

import java.util.ArrayList;
import java.util.List;

public class UC {

    private String codigo;
    private String nome;

    private List<TurnoT> turnosT = new ArrayList<>();
    private List<TurnoPL> turnosPL = new ArrayList<>();
    private List<TurnoTP> turnosTP = new ArrayList<>();

    public UC(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public UC(String[] values, int startIndex){
        this.codigo = values[startIndex++];
        this.nome = values[startIndex];
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void addTurnoT(TurnoT t){
        this.turnosT.add(t);
    }

    public void addTurnoTP(TurnoTP t){
        this.turnosTP.add(t);
    }

    public void addTurnoPL(TurnoPL t){
        this.turnosPL.add(t);
    }

    public List<TurnoT> getTurnosT() {
        return new ArrayList<>(turnosT);
    }

    public List<TurnoPL> getTurnosPL() {
        return new ArrayList<>(turnosPL);
    }

    public List<TurnoTP> getTurnosTP() {
        return new ArrayList<>(turnosTP);
    }

    public boolean turnosTIsEmpty(){
        return turnosT.isEmpty();
    }

    public boolean turnosPLIsEmpty(){
        return turnosPL.isEmpty();
    }

    public boolean turnosTPIsEmpty(){
        return turnosTP.isEmpty();
    }
}
