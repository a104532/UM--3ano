package ui;

import business.*;
import business.SubHorarios.Horario;
import business.SubUtilizadores.Aluno;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TextUI {
    private ILNGesHorarios model;
    private Scanner scin;

    public TextUI() {
        this.model = new LNGesHorariosFacade();
        this.scin = new Scanner(System.in);
    }

    public void menuLogin() {
        Menu menu = new Menu("SISTEMA DE GESTÃO DE HORÁRIOS",new String[]{
                "Log in"
        });

        menu.setHandler(1, this :: login);

        menu.runOnce();
    }

    private void login(){
        clearTerminal();
        try {
            System.out.print("*** " + "LOGIN" + " ***\n");
            System.out.println("Introduza o email institucional: ");
            String email = scin.nextLine();
            System.out.println("Introduza a password: ");
            String password = scin.nextLine();

            if (this.model.validateCredentials(email, password)) {
                if(this.model.isDC())
                    menuDC();
                else
                    menuAluno();
            }
            else{
                System.out.println("Não foi possível iniciar sessão. Dados inválidos!");
                pressAnyKey();
                menuLogin();
            }
        } catch (Exception ignored) {}
    }

    private void menuAluno(){
        clearTerminal();
        Menu menu = new Menu("MENU ALUNO",new String[]{
                "Consultar horário",
                "Exportar horário"
        });

        menu.setHandler(1, this :: consultarHorario);
        menu.setHandler(2, this :: exportarHorario);

        menu.run();
    }

    private void menuDC() {
        clearTerminal();
        Menu menu = new Menu("MENU DC",new String[]{
                "Importar alunos/ucs",
                "Importar salas",
                "Importar Turnos",
                "Gerar horário",
                "Alocar manualmente"
        });

        menu.setPreCondition(3, () -> this.model.alunosCarregados());
        menu.setPreCondition(3, () -> this.model.salasCarregadas());
        menu.setPreCondition(4, () -> this.model.alunosCarregados());
        menu.setPreCondition(4, () -> this.model.turnosCarregados());
        menu.setPreCondition(5, () -> this.model.horariosGerados());

        menu.setHandler(1, () -> importarDados('A'));
        menu.setHandler(2, () -> importarDados('S'));
        menu.setHandler(3, () -> importarDados('T'));
        menu.setHandler(4, this :: menuGerarHorarios);
        menu.setHandler(5, this :: menuAlocarManualmente);

        menu.run();
    }

    private void consultarHorario(){
        clearTerminal();
        try{
            System.out.println("*** " +"HORÁRIO" + " ***\n");
            System.out.println(this.model.consultarHorario("a"));
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao apresentar o horário.");
        }
    }

    public void exportarHorario(){
        clearTerminal();
        try{
            System.out.println("*** " +"EXPORTAR HORÁRIO"+ " ***\n");
            this.model.exportarHorario();
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao exportar o horário.");
        }

        System.out.println("\n Horário exportado");

        pressAnyKey();
    }

    public void importarDados(char c){
        clearTerminal();
        System.out.println("*** " +"IMPORTAR DADOS"+ " ***\n");
        try{
            this.model.importarDados(c);
        }catch(Exception e){
            System.out.println("Ocorreu um erro ao importar dados.");
        }


        if(c == 'A')
            System.out.println("Alunos e UCs importados");
        else if(c == 'S')
            System.out.println("Salas importadas");
        else
            System.out.println("Turnos importados");

        pressAnyKey();
    }

    public void menuGerarHorarios(){
        clearTerminal();
        Menu menu = new Menu("GERAR HORÁRIOS",new String[]{
                "Alocar alunos por média",
                "Alocar alunos aleatóriamente"
        });

        menu.setHandler(1, () -> gerarHorarios(true));
        menu.setHandler(2, () -> gerarHorarios(false));

        menu.runOnce();
    }

    private void gerarHorarios(boolean pormedia){
        clearTerminal();
        System.out.println("*** " +"GERAR HORÁRIOS"+ " ***\n");
        try{
            this.model.gerarHorarios(pormedia);
        }catch (Exception e) {
            System.out.println("Ocorreu um erro ao gerar horários.");
        }

        System.out.println("Horários gerados");

        pressAnyKey();
    }

    private void menuAlocarManualmente(){
        clearTerminal();
        Menu menu = new Menu("ALOCAR MANUALMENTE",new String[]{
                "Listar alunos não alocados",
                "Alocar aluno"
        });

        menu.setHandler(1, this :: alunosNaoAlocados);
        menu.setHandler(2, this :: alocarAluno);

        menu.run();
    }

    private void alunosNaoAlocados(){
        clearTerminal();
        System.out.println("*** " +"ALUNOS NÃO ALOCADOS"+" ***\n");
        try{

            List<String> naoAlocados = this.model.listarNaoAlocados();
            for(String s : naoAlocados)
                System.out.println(s);

        }catch (Exception e) {
            System.out.println("Ocorreu um erro ao mostrar alunos não alocados");
        }

        pressAnyKey();
    }

    private void alocarAluno(){
        clearTerminal();
        System.out.println("*** " +"ALOCAR MANUALMENTE"+" ***\n");

        System.out.println("Introduza o email do aluno que quer alocar: ");
        String email = scin.nextLine();
        clearTerminal();

        String horario = this.model.consultarHorario(email);

        if(horario.equals("Aluno não existe") || horario.equals("Aluno não possui horário")){
            System.out.println("\n"+horario);
            pressAnyKey();
        }
        else{
            System.out.println("Horário incompleto do aluno: \n");

            System.out.println(horario);

            System.out.println("\nUCs não alocadas:");
            Map<String, List<String>> ucsNaoAlocadas = this.model.ucsNaoAlocadas(email);
            for(Map.Entry<String, List<String>> entry : ucsNaoAlocadas.entrySet()){
                String[] nomeCodigo = entry.getKey().split(";");
                for(String tipoTurno : entry.getValue()){
                    System.out.println(nomeCodigo[0] + " " + nomeCodigo[1] + " - turno " + tipoTurno);
                }
            }

            boolean exit = false;
            while (!exit) {
                System.out.println("\nIntroduza o código da UC que quer alocar: ");
                String uc = scin.nextLine();
                String key = "";
                boolean contains = false;
                for(String keyUC : ucsNaoAlocadas.keySet()) {
                    String[] nomeCodigo = keyUC.split(";");
                    if (nomeCodigo[0].equals(uc)) {
                        contains = true;
                        key = keyUC;
                        break;
                    }
                }
                if (!contains) {
                    System.out.println("UC inválida.");
                    exit = true;
                    continue;
                }

                List<String> tiposTurnosFalta = ucsNaoAlocadas.get(key);
                int numTurnosRestantes = tiposTurnosFalta.size();
                String tipoTurno;
                if(numTurnosRestantes > 1) {
                    System.out.println("\nTurno teórico ou TP/PL: (T/TPPL)");
                    tipoTurno = scin.nextLine();
                } else{
                    tipoTurno = tiposTurnosFalta.getFirst();
                }

                List<String> turnos = new ArrayList<>();
                if(tipoTurno.equals("TP/PL")) { tipoTurno = "TPPL"; }
                if(tipoTurno.equalsIgnoreCase("T") || tipoTurno.equalsIgnoreCase("TPPL"))
                    turnos = this.model.getTurnosAlocar(email, uc, tipoTurno);

                System.out.println("Turnos: ");
                for(String s : turnos)
                    System.out.println(s);

                System.out.println("\nIntroduza o número do turno que quer alocar: ");
                String turno = scin.nextLine();
                boolean startsWith = false;
                for(String s : turnos){
                    if (s.startsWith(turno)) {
                        startsWith = true;
                        break;
                    }
                }
                if(!startsWith) {
                    System.out.println("Turno inválido.");
                    exit = true;
                    continue;
                }

                this.model.alocarManualmente(email,Integer.parseInt(turno));
                System.out.println("Aluno alocado no turno.");
                exit = true;
                //numTurnosRestantes--;

                /*if(numTurnosRestantes > 0){
                    System.out.println("\nDeseja alocar outro turno? (S/N)");
                    if(scin.nextLine().equalsIgnoreCase("N"))
                        exit = true;
                }
                else{
                    exit = true;
                }*/
            }
        }
    }

    private void clearTerminal(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pressAnyKey(){
        System.out.println("\nPress any key to continue");
        scin.nextLine();
    }
}