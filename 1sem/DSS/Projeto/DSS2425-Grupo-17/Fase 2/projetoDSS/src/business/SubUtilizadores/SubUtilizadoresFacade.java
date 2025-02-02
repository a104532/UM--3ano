package business.SubUtilizadores;

import business.SubHorarios.Horario;
import business.SubHorarios.Turno;
import data.AlunoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubUtilizadoresFacade implements ISubUtilizadores {

    private Map<String, Aluno> alunos;
    private final String emailDC = "1";
    private final String passwordDC = "1";
    private String emailAutenticado = null; //email

    public SubUtilizadoresFacade(){
        this.alunos = AlunoDAO.getInstance();
    }

    public boolean autenticarUtilizador(String email, String password) {
        if(email.equals(emailDC) && password.equals(passwordDC)){
            emailAutenticado = emailDC;
            return true;
        }

        Aluno aux = alunos.get(email);
        if(aux != null && aux.getPassword().equals(password)){
            emailAutenticado = email;
            return true;
        }
        return false;
    }

    public boolean isDC(){
        return emailDC.equals(emailAutenticado);
    }

    @Override
    public void logout() {
        emailAutenticado = null;
    }

    public Aluno getAlunoAutenticado() {
        return alunos.get(emailAutenticado);
    }

    public void adicionarAluno(Aluno aluno){
        this.alunos.put(aluno.getEmail(),aluno);
    }

    public Aluno getAluno(String email){
        return this.alunos.get(email);
    }

    public boolean existeAluno(String email){
        return alunos.containsKey(email);
    }

    public List<Aluno> getAlunos(){
        return new ArrayList<>(alunos.values());
    }

    public boolean alunosCarregados(){
        return !alunos.isEmpty();
    }

    public void allNaoAlocado(){
        for(Aluno aluno : alunos.values()){
            if(aluno.isAlocado()){
                aluno.setAlocado(false);
                alunos.put(aluno.getEmail(),aluno);
            }
        }
    }

    public void atualizaAlunos(Set<Aluno> alocados){
        for(Aluno aluno : alocados){
            this.alunos.put(aluno.getEmail(),aluno);
        }
    }

    public List<Aluno> listarNaoAlocados(){
        List<Aluno> naoalocados = new ArrayList<>();

        for(Aluno aluno : alunos.values()){
            if(!aluno.isAlocado())
                naoalocados.add(aluno);
        }

        return naoalocados;
    }
}
