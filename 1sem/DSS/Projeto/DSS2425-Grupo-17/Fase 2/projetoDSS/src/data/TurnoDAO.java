package data;

import business.SubHorarios.*;
import business.SubUtilizadores.Aluno;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class TurnoDAO implements Map<Integer, Turno> {

    private static TurnoDAO singleton = null;

    private TurnoDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS salas (" +
                    "Id varchar(10) NOT NULL PRIMARY KEY," +
                    "Edificio int(3) NOT NULL," +
                    "Num varchar(10) NOT NULL," +
                    "Capacidade int(4) DEFAULT 0)";
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS turnos (" +
                    "Num int(3) NOT NULL PRIMARY KEY," +
                    "Tipo varchar(10) NOT NULL," +
                    "Limite int(3) DEFAULT 0," +
                    "Dia int(2) NOT NULL," +
                    "Hora varchar(10) NOT NULL," +
                    "Uc varchar(10) NOT NULL," +
                    "NrAlocados int(3) NOT NULL, " +
                    "Sala varchar(10), foreign key(Sala) references salas(Id))";
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS turnoUC (" +
                    "NumTurno int(3) NOT NULL," +
                    "CodigoUC varchar(10) NOT NULL," +
                    "PRIMARY KEY (NumTurno, CodigoUC)," +
                    "foreign key(NumTurno) references turnos(Num) ON DELETE CASCADE," +
                    "foreign key(CodigoUC) references ucs(Codigo) ON DELETE CASCADE)";


            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public static TurnoDAO getInstance() {
        if (TurnoDAO.singleton == null) {
            TurnoDAO.singleton = new TurnoDAO();
        }
        return TurnoDAO.singleton;
    }


    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM turnos")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return this.size()==0;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT Num FROM turnos WHERE Num=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                r = rs.next();
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        Turno t = (Turno) value;
        return this.containsKey(t.getNumero());
    }

    @Override
    public Turno get(Object key) {
        Turno t = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "SELECT t.*, s.Edificio, s.Num as SalaNum, s.Capacidade " +
                             "FROM turnos t " +
                             "LEFT JOIN salas s ON t.Sala = s.Id " +
                             "WHERE t.Num = ?")) {

            pstm.setInt(1, (Integer) key);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    int num = rs.getInt("Num");
                    String tipo = rs.getString("Tipo");
                    int limite = rs.getInt("Limite");
                    DayOfWeek dia = DayOfWeek.of(rs.getInt("Dia"));
                    LocalTime hora = LocalTime.parse(rs.getString("Hora"));
                    int nrAlocados = rs.getInt("NrAlocados");
                    String uc = rs.getString("Uc");

                    if(tipo.equals("T")){
                        t =  new TurnoT(num,dia,hora,uc,nrAlocados);
                    }
                    else if(tipo.equals("TP")){
                        t = new TurnoTP(num,dia,hora,limite,uc,nrAlocados);
                    }
                    else{
                        t = new TurnoPL(num,dia,hora,limite,uc,nrAlocados);
                    }

                    String salaId = rs.getString("Sala");
                    if (salaId != null) {
                        int edificio = rs.getInt("Edificio");
                        String salanum = rs.getString("SalaNum");
                        int capacidade = rs.getInt("Capacidade");
                        t.setSala(new Sala(salaId,edificio,salanum,capacidade));
                    }

                    return t;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public Turno put(Integer key, Turno turno) {
        Turno t = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)){
             conn.setAutoCommit(false);
            try {
                PreparedStatement psSala = conn.prepareStatement(
                        "INSERT INTO salas (Id,Edificio,Num,Capacidade) VALUES (?,?,?,?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "Edificio = VALUES(Edificio), " +
                                "Num = VALUES(Num), " +
                                "Capacidade = VALUES(Capacidade)");

                psSala.setString(1,turno.getSala().getIdentificador());
                psSala.setInt(2,turno.getSala().getEdificio());
                psSala.setString(3,turno.getSala().getNumero());
                psSala.setInt(4,turno.getSala().getCapacidade());

                psSala.executeUpdate();

                PreparedStatement psTurno = conn.prepareStatement(
                    "INSERT INTO turnos (Num,Tipo,Limite,Dia,Hora,Uc,NrAlocados,Sala) VALUES (?,?,?,?,?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "Tipo = VALUES(Tipo), " +
                            "Limite = VALUES(Limite), " +
                            "Dia = VALUES(Dia), " +
                            "Hora = VALUES(Hora), " +
                            "Uc = VALUES(Uc), " +
                            "NrAlocados = VALUES(NrAlocados), " +
                            "Sala = VALUES(Sala)");

                psTurno.setInt(1, key);
                psTurno.setString(2, turno.getTipo());
                psTurno.setInt(3, turno.getLimite());
                psTurno.setInt(4,turno.getDiaSemana().getValue());
                psTurno.setString(5, turno.getHora().toString());
                psTurno.setString(6, turno.getUc());
                psTurno.setInt(7, turno.getNralocados());
                psTurno.setString(8, turno.getSala().getIdentificador());
                psTurno.executeUpdate();

                PreparedStatement psTurnoUC = conn.prepareStatement(
                        "INSERT IGNORE INTO turnoUC (NumTurno, codigoUC) VALUES (?, ?)");

                psTurnoUC.setInt(1, key);
                psTurnoUC.setString(2, turno.getUc());
                psTurnoUC.executeUpdate();

                conn.commit();
                return t;
            }catch (SQLException e) {
                conn.rollback();
                throw e;
            }finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Turno remove(Object key) {
        Turno t = get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM turnos WHERE Num = ?")) {

            ps.setInt(1,(Integer) key);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return t;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Turno> turnos) {
        for(Turno t : turnos.values()) {
            this.put(t.getNumero(), t);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            stm.executeUpdate("DELETE FROM turnos");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<Integer> keySet() {
        return null;
    }

    @Override
    public Collection<Turno> values() {
        Collection<Turno> turnos = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * from turnos")){

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    int num = rs.getInt("Num");
                    turnos.add(get(num));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return turnos;
    }

    @Override
    public Set<Entry<Integer, Turno>> entrySet() {
        return null;
    }
}
