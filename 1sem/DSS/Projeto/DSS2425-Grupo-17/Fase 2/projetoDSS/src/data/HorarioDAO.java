package data;

import business.SubHorarios.Horario;
import business.SubHorarios.Turno;
import business.SubHorarios.UC;

import java.sql.*;
import java.util.*;

public class HorarioDAO implements Map<String, Horario> {

    private static HorarioDAO singleton = null;

    private HorarioDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS horarios (" +
                    "Id varchar(45) NOT NULL PRIMARY KEY)";
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS horarioTurno (" +
                    "IdHorario varchar(45) NOT NULL," +
                    "NumTurno int(3) NOT NULL," +
                    "PRIMARY KEY (IdHorario, NumTurno)," +
                    "foreign key(IdHorario) references horarios(Id) ON DELETE CASCADE," +
                    "foreign key(NumTurno) references turnos(Num) ON DELETE CASCADE)";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static HorarioDAO getInstance() {
        if (HorarioDAO.singleton == null) {
            HorarioDAO.singleton = new HorarioDAO();
        }
        return HorarioDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM horarios")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT Id FROM horarios WHERE Codigo=?")) {
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
        UC uc = (UC) value;
        return this.containsKey(uc.getCodigo());
    }

    @Override
    public Horario get(Object key) {
        Horario h=null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM horarios WHERE Id = ?");
            ps.setString(1, (String) key);
            try(ResultSet rs = ps.executeQuery();){
                if (rs.next()) {

                    h = new Horario(rs.getString("Id"));

                    try(PreparedStatement psTurnos = conn.prepareStatement(
                            "SELECT t.* FROM turnos t " +
                                    "JOIN horarioTurno ht ON t.Num = ht.NumTurno " +
                                    "WHERE ht.IdHorario = ?")){

                        psTurnos.setString(1, (String) key);
                        try(ResultSet rsTurnos = psTurnos.executeQuery();){
                            TurnoDAO turnoDAO = TurnoDAO.getInstance();

                            while (rsTurnos.next()) {
                                Turno turno = turnoDAO.get(rsTurnos.getInt("Num"));
                                h.addTurno(turno);
                            }
                        }
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return h;
    }

    @Override
    public Horario put(String key, Horario value) {
        Horario h = get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psHorario = conn.prepareStatement(
                        "INSERT INTO horarios (Id) VALUES (?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "Id = VALUES(Id)");

                psHorario.setString(1, value.getId());
                psHorario.executeUpdate();

                PreparedStatement psDelete = conn.prepareStatement(
                        "DELETE FROM horarioTurno WHERE IdHorario = ?");
                psDelete.setString(1, value.getId());
                psDelete.executeUpdate();

                PreparedStatement psHorarioTurno = conn.prepareStatement(
                        "INSERT INTO horarioTurno (IdHorario, NumTurno) VALUES (?,?)");

                for (Turno turno : value.getListTurnos()) {
                    psHorarioTurno.setString(1, value.getId());
                    psHorarioTurno.setInt(2, turno.getNumero());
                    psHorarioTurno.executeUpdate();
                }

                conn.commit();
                return h;

            } catch (SQLException e) {
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
    public Horario remove(Object key) {
        Horario h = get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {

            PreparedStatement psHorarioTurno = conn.prepareStatement("DELETE FROM horarioTurno WHERE IdHorario = ?");
            psHorarioTurno.setString(1, (String) key);
            psHorarioTurno.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return h;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Horario> horarios) {
        for(Horario horario : horarios.values()) {
            this.put(horario.getId(), horario);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {
            conn.setAutoCommit(false);
            try {
                Statement stmHorarioTurno = conn.createStatement();
                stmHorarioTurno.executeUpdate("DELETE FROM horarioTurno");

                Statement stmHorario = conn.createStatement();
                stmHorario.executeUpdate("DELETE FROM horarios");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Horario> values() {
        Collection<Horario> horarios = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {
            Statement stm = conn.createStatement();
            try(ResultSet rsHorarios = stm.executeQuery("SELECT * FROM horarios")){
                while (rsHorarios.next()) {
                    String horarioId = rsHorarios.getString("Id");
                    horarios.add(this.get(horarioId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return horarios;
    }

    @Override
    public Set<Entry<String, Horario>> entrySet() {
        return null;
    }
}
