package business.SubHorarios;
import business.SubUtilizadores.Aluno;
import java.util.List;
import java.util.Set;

public interface ISubHorarios {

    Set<Aluno> gerarHorarios(List<Aluno> alunos, boolean porMedia);

    boolean validarHorario();

    void alocarManualmente(String id, int ntTurno);

    String converterHorario(Aluno aluno);

    void exportarHorario(String emailAluno);

    List<String> getTurnosAlocar(String id, String uc,String tipo);

    void adicionarUC(UC uc);

    UC getUC(String codigo);

    boolean existeUC(String codigo);

    void adicionarHorario(Horario h);

    Horario getHorario(String id);

    boolean existeHorario(String id);

    void adicionarSala(Sala sala);

    void adicionarTurno(Turno t);

    Turno getTurno(int numero);

    Sala getSala(String id);

    boolean ucsCarregadas();

    boolean salasCarregadas();

    boolean turnosCarregados();

    boolean horariosGerados();
}
