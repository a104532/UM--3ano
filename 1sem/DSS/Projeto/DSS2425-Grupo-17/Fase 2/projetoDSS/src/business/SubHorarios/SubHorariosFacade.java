package business.SubHorarios;
import business.SubUtilizadores.Aluno;
import data.HorarioDAO;
import data.TurnoDAO;
import data.UCDAO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.Duration;
import java.util.stream.Collectors;

public class SubHorariosFacade implements ISubHorarios{

    private Map<String,Sala> salas;
    private Map<String,UC> ucs;
    private Map<Integer,Turno> turnos;
    private Map<String,Horario> horarios;

    private Set<Aluno> Alocados = new HashSet<>();

    public SubHorariosFacade(){
        this.salas = new HashMap<>();
        this.ucs = UCDAO.getInstance();
        this.turnos = TurnoDAO.getInstance();
        this.horarios = HorarioDAO.getInstance();
    }

    @Override
    public Set<Aluno> gerarHorarios(List<Aluno> alunos, boolean porMedia) {

        List<Aluno> alunosComEstatuto = new ArrayList<>(alunos.stream().filter(aluno -> !aluno.RegimeToString().isEmpty()).toList());

        if(porMedia){
            alunosComEstatuto.sort((a1, a2) -> Double.compare(a2.getMedia(), a1.getMedia()));
            alunos.sort((a1, a2) -> Double.compare(a2.getMedia(), a1.getMedia()));
        }
        else{
            Collections.shuffle(alunosComEstatuto);
            Collections.shuffle(alunos);
        }

        for(Aluno a : alunosComEstatuto)
            alocarAluno(a);

        for(Aluno a : alunos){
            if(!alunosComEstatuto.contains(a))
                alocarAluno(a);
        }

        return Alocados;
    }

    private void alocarAluno(Aluno aluno){
        Horario horario = new Horario(aluno.getEmail());

        boolean alocadototalmente = true;
        List<UC> ucsAluno = aluno.getUcs();
        Collections.shuffle(ucsAluno);

        for (UC uc1 : ucsAluno) {

            String codigo = uc1.getCodigo();
            UC uc = ucs.get(codigo);
            boolean alocadoTeorica = false;
            boolean alocadoPratica = false;

            if (uc != null) {

                for (Turno turno : uc.getTurnosT()) {
                    if (podeAlocarNr(turno) && podeAlocarTempo(horario, turno)) {
                        turno.accNralocados();
                        horario.addTurno(turno);
                        turnos.put(turno.getNumero(), turno);
                        alocadoTeorica = true;
                        break;
                    }
                }

                if(!uc.turnosPLIsEmpty()){
                    for (Turno turno : uc.getTurnosPL()) {
                        if (podeAlocarNr(turno)&&podeAlocarTempo(horario,turno)) {
                            turno.accNralocados();
                            horario.addTurno(turno);
                            turnos.put(turno.getNumero(), turno);
                            alocadoPratica = true;
                            break;
                        }
                    }
                }
                else{
                    for (Turno turno : uc.getTurnosTP()) {
                        if (podeAlocarNr(turno)&&podeAlocarTempo(horario,turno)) {
                            turno.accNralocados();
                            horario.addTurno(turno);
                            turnos.put(turno.getNumero(), turno);
                            alocadoPratica = true;
                            break;
                        }
                    }
                }
            }

            if (!alocadoTeorica || !alocadoPratica) {
                alocadototalmente = false;
                break;
            }
        }

        if (alocadototalmente) {
            aluno.setAlocado(true);
            Alocados.add(aluno);
        }

        horarios.put(horario.getId(),horario);

    }

    private boolean podeAlocarNr(Turno novoturno) {
        boolean alocado = novoturno.getNralocados() < novoturno.getLimite();
        return alocado;
    }

    private boolean podeAlocarTempo(Horario horario, Turno novoturno){
        List<Turno> turnosHorario = horario.getListTurnos();
        for (Turno t : turnosHorario) { // assumir que as aulas duram sempre 2 horas
            if(t.getDiaSemana().equals(novoturno.getDiaSemana())) {

                long diferencaMinutos = Duration.between(t.getHora(), novoturno.getHora()).abs().toMinutes();

                if(diferencaMinutos < 120){
                    return false;
                }


            }
        }

        return true;
    }

    @Override
    public boolean validarHorario() {
        return false;
    }

    @Override
    public void alocarManualmente(String id, int nrTurno) {
        Horario h = horarios.get(id);
        Turno turno = turnos.get(nrTurno);
        h.addTurno(turno);
        horarios.put(id,h);
        turno.accNralocados();
        turnos.put(turno.getNumero(), turno);
    }

    @Override
    public String converterHorario(Aluno aluno) {
        Horario h = horarios.get(aluno.getNumero());
        return h.toString();
    }

    @Override
    public void exportarHorario(String emailAluno) {
        Horario h = horarios.get(emailAluno);
        try (FileWriter writer = new FileWriter("horario.txt")) {
            writer.write(h.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTurnosAlocar(String id, String uc,String tipo){
        Horario h = this.horarios.get(id);

        List<Turno> ts = new ArrayList<>();

        if(tipo.equalsIgnoreCase("T"))
            ts = turnos.values().stream().filter(t -> t.getUc().equals(uc) && t instanceof TurnoT).toList();
        else if(tipo.equalsIgnoreCase("TPPL"))
            ts = turnos.values().stream().filter(t -> t.getUc().equals(uc) && (t instanceof TurnoTP || t instanceof  TurnoPL)).toList();

        List<String> res = new ArrayList<>();
        String aux = "";

        for(Turno t : ts){
            if(!podeAlocarTempo(h,t) && !podeAlocarNr(t))
                aux = " : limite de alunos do turno excedido e sobrepõe outro turno do horário";
            else if(!podeAlocarTempo(h,t))
                aux = " : sobrepõe outro turno do horário";
            else if(!podeAlocarNr(t))
                aux = " : limite de alunos do turno excedido";
            res.add(t.getNumero() + " - " + t.getDiaSemana() +" "+ t.getHora() + aux);
        }

        return res;
    }

    public void adicionarUC(UC uc){
        this.ucs.put(uc.getCodigo(),uc);
    }

    public UC getUC(String codigo){
        return this.ucs.get(codigo);
    }

    public boolean existeUC(String codigo){
        return ucs.containsKey(codigo);
    }

    public void adicionarHorario(Horario h){
        this.horarios.put(h.getId(),h);
    }

    public Horario getHorario(String id){
        return this.horarios.get(id);
    }

    public boolean existeHorario(String id){
        return horarios.containsKey(id);
    }

    public void adicionarSala(Sala sala){
        this.salas.put(sala.getIdentificador(),sala);
    }

    public Sala getSala(String id){
        return salas.get(id);
    }

    public void adicionarTurno(Turno t){
        this.turnos.put(t.getNumero(),t);
    }

    public Turno getTurno(int numero){
        return turnos.get(numero);
    }

    public boolean ucsCarregadas(){
        return !ucs.isEmpty();
    }

    public boolean salasCarregadas(){
        return !salas.isEmpty();
    }

    public boolean turnosCarregados(){
        return !turnos.isEmpty();
    }

    public boolean horariosGerados(){
        return !horarios.isEmpty();
    }
}
