package business.SubUtilizadores;

import business.SubHorarios.Horario;
import business.SubHorarios.Turno;

import java.util.List;
import java.util.Set;

public interface ISubUtilizadores {

    boolean autenticarUtilizador(String email, String password);

    boolean isDC();

    void logout();

    Aluno getAlunoAutenticado();

    void adicionarAluno(Aluno aluno);

    Aluno getAluno(String email);

    boolean existeAluno(String email);

    List<Aluno> getAlunos();

    boolean alunosCarregados();

    void allNaoAlocado();

    void atualizaAlunos(Set<Aluno> alocados);

    List<Aluno> listarNaoAlocados();
}
