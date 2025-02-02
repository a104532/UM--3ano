package business;

import business.SubHorarios.Horario;
import business.SubUtilizadores.Aluno;

import java.util.List;
import java.util.Map;

public interface ILNGesHorarios {

    boolean validateCredentials(String email, String password);

    boolean alunosCarregados();

    boolean salasCarregadas();

    boolean turnosCarregados();

    boolean horariosGerados();

    boolean isDC();

    void importarDados(char tipo);

    void gerarHorarios(boolean porMedia);

    List<String> listarNaoAlocados();

    Map<String, List<String>> ucsNaoAlocadas(String email);

    List<String> getTurnosAlocar(String id,String uc, String tipo);

    void alocarManualmente(String email,int nrTurno);

    String consultarHorario(String email);

    void exportarHorario();
}
