package business;

import business.SubHorarios.*;
import business.SubUtilizadores.Aluno;
import business.SubUtilizadores.ISubUtilizadores;
import business.SubUtilizadores.SubUtilizadoresFacade;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class LNGesHorariosFacade implements ILNGesHorarios {
    private ISubHorarios subHorarios;
    private ISubUtilizadores subUtilizadores;

    private final String filePathAlunos = "alunos.csv";
    private final String filePathSalas = "salas.csv";
    private final String filePathTurnos = "turnos.csv";
    private boolean alunosCarregados;
    private boolean salasCarregadas;
    private boolean turnosCarregados;
    private boolean horariosGerados;

    public LNGesHorariosFacade() {
        this.subHorarios = new SubHorariosFacade();
        this.subUtilizadores = new SubUtilizadoresFacade();
        this.alunosCarregados = subUtilizadores.alunosCarregados();
        this.salasCarregadas = subHorarios.salasCarregadas();
        this.turnosCarregados = subHorarios.turnosCarregados();
        this.horariosGerados = subHorarios.horariosGerados();
    }

    public boolean validateCredentials(String email, String password) {
        return subUtilizadores.autenticarUtilizador(email, password);
    }

    public boolean alunosCarregados() {
        return alunosCarregados;
    }

    public boolean salasCarregadas() {
        if(!salasCarregadas)
            return turnosCarregados;
        return true;
    }

    public boolean turnosCarregados() {
        return turnosCarregados;
    }

    public boolean horariosGerados() {
        return horariosGerados;
    }

    public boolean isDC(){
        return subUtilizadores.isDC();
    }

    public void importarDados(char tipo){
        String fileName;
        if(tipo == 'A'){
            fileName = filePathAlunos;
        } else if(tipo == 'S'){
            fileName = filePathSalas;
        } else{
            fileName = filePathTurnos;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",\\s*",-1);

                if (tipo == 'A') {
                    UC uc = new UC(values, 0);
                    Aluno aluno = new Aluno(values, 2);
                    aluno.addUC(uc);
                    this.subHorarios.adicionarUC(uc);
                    this.subUtilizadores.adicionarAluno(aluno);

                } else if (tipo == 'S') {
                    Sala sala = new Sala(values, 0);
                    this.subHorarios.adicionarSala(sala);
                } else {
                    String salaId = values[4];
                    String tipoTurno = values[5];
                    Sala salaTurno = this.subHorarios.getSala(salaId);
                    Turno t;
                    if (tipoTurno.equals("T")) {
                        t = new TurnoT(values);
                    } else if (tipoTurno.equals("TP")) {
                        t = new TurnoTP(values);
                    } else {
                        t = new TurnoPL(values);
                    }
                    t.setSala(salaTurno);
                    this.subHorarios.adicionarTurno(t);
                }
            }

            if(tipo == 'A'){
                this.alunosCarregados = true;
            } else if(tipo == 'S'){
                this.salasCarregadas = true;
            } else{
                this.turnosCarregados = true;
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

    }

    public void gerarHorarios(boolean porMedia){
        //this.subUtilizadores.allNaoAlocado();
        List<Aluno> alunos = subUtilizadores.getAlunos();
        Set<Aluno> alocados = subHorarios.gerarHorarios(alunos,porMedia);
        this.subUtilizadores.atualizaAlunos(alocados);
        horariosGerados = true;
    }

    public List<String> listarNaoAlocados(){
        List<String> naoalocados = new ArrayList<>();
        for(Aluno a :this.subUtilizadores.listarNaoAlocados())
            naoalocados.add(a.getEmail() + " " + a.getNome());

        return naoalocados;
    }

    public Map<String, List<String>> ucsNaoAlocadas(String email) {

        List<Turno> turnosHorario = this.subHorarios.getHorario(email).getListTurnos();
        List<UC> ucs = this.subUtilizadores.getAluno(email).getUcs();

        Map<String, List<String>> ucsNaoAlocadas = new HashMap<>();

        for (UC uc : ucs) {
            String ucCode = uc.getCodigo();
            boolean hasTurnoT = false;
            boolean hasTurnoTPorPL = false;

            for (Turno turno : turnosHorario) {
                if (ucCode.equals(turno.getUc())) {
                    if (turno instanceof TurnoT) {
                        hasTurnoT = true;
                    } else if (turno instanceof TurnoTP || turno instanceof TurnoPL) {
                        hasTurnoTPorPL = true;
                    }
                }
            }

            if (!hasTurnoT && !hasTurnoTPorPL) {
                ucsNaoAlocadas.put(ucCode + ";" + uc.getNome(), List.of("T", "TP/PL"));
            }
            else if (!hasTurnoTPorPL) {
                ucsNaoAlocadas.put(ucCode + ";" + uc.getNome(), List.of("TP/PL"));
            }
            else if (!hasTurnoT) {
                ucsNaoAlocadas.put(ucCode + ";" + uc.getNome(), List.of("T"));
            }
        }

        return ucsNaoAlocadas;
    }

    public List<String> getTurnosAlocar(String id,String uc, String tipo){
        return this.subHorarios.getTurnosAlocar(id,uc,tipo);
    }

    public void alocarManualmente(String email,int nrTurno){
        this.subHorarios.alocarManualmente(email,nrTurno);
        if(ucsNaoAlocadas(email).isEmpty()){
            Aluno a = this.subUtilizadores.getAluno(email);
            a.setAlocado(true);
            this.subUtilizadores.adicionarAluno(a);
        }
    }

    public String consultarHorario(String email){
        Aluno aluno;
        if(email.equals("a"))
            aluno = this.subUtilizadores.getAlunoAutenticado();
        else
            aluno = this.subUtilizadores.getAluno(email);

        if(aluno == null){
            return "Aluno não existe";
        }
        Horario h =  this.subHorarios.getHorario(aluno.getEmail());

        if(h==null)
            return "Aluno não possui horário";


        return h.toString();
    }

    public void exportarHorario(){
        Aluno alunoAutenticado = this.subUtilizadores.getAlunoAutenticado();
        subHorarios.exportarHorario(alunoAutenticado.getEmail());
    }


}