package business.SubHorarios;

public class Sala {

    private String identificador;
    private int edificio;
    private String numero;
    private int capacidade; 

    public Sala(String identificador, int edificio, String numero, int capacidade) {
        this.identificador = identificador;
        this.edificio = edificio;
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public Sala(String[] values, int startIndex){
        this.identificador = values[startIndex++];
        this.edificio = Integer.parseInt(values[startIndex++]);
        this.numero = values[startIndex++];
        this.capacidade = Integer.parseInt(values[startIndex]);
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getEdificio() {
        return edificio;
    }

    public void setEdificio(int edificio) {
        this.edificio = edificio;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    @Override
    public String toString() {
        return ("Ed " + edificio + " "+ numero);
    }
}
