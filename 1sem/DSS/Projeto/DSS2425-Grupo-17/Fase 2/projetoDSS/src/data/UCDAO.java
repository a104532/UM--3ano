package data;

import business.SubHorarios.*;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class UCDAO implements Map<String, UC> {

    private static UCDAO singleton = null;

    private UCDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS ucs (" +
                    "Codigo varchar(10) NOT NULL PRIMARY KEY," +
                    "Nome varchar(45) NOT NULL)";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public static UCDAO getInstance() {
        if (UCDAO.singleton == null) {
            UCDAO.singleton = new UCDAO();
        }
        return UCDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM ucs")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT Codigo FROM ucs WHERE Codigo=?")) {
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
    public UC get(Object key) {
        UC uc = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM ucs WHERE Codigo=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String codigo = rs.getString("Codigo");  // Podíamos usar a key, mas assim temos a certeza que é o id da BD
                    String nome = rs.getString("Nome");

                    // Reconstruir a turma cokm os dados obtidos da BD
                    uc = new UC(codigo,nome);

                    PreparedStatement psUCs = conn.prepareStatement("SELECT NumTurno FROM turnoUC WHERE CodigoUC = ?");
                    psUCs.setString(1, key.toString());
                    ResultSet rsUCs = psUCs.executeQuery();

                    TurnoDAO turnos = TurnoDAO.getInstance();
                    while (rsUCs.next()) {
                        Turno t = turnos.get(rsUCs.getInt("NumTurno"));
                        if(t.getTipo().equals("T"))
                            uc.addTurnoT((TurnoT) t);
                        else if(t.getTipo().equals("TP"))
                            uc.addTurnoTP((TurnoTP) t);
                        else
                            uc.addTurnoPL((TurnoPL) t);
                    }
                    return uc;
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return uc;
    }

    @Override
    public UC put(String key, UC value) {
        UC uc = null;
        String sql = "INSERT INTO ucs (Codigo,Nome) VALUES (?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "Nome = VALUES(Nome)";

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, value.getCodigo());
            pstm.setString(2, value.getNome());

            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error while saving UC: " + e.getMessage(), e);
        }

        return uc;
    }

    @Override
    public UC remove(Object key) {
        UC uc = get(key);
        if (uc != null) {
            try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
                 PreparedStatement pstm = conn.prepareStatement("DELETE FROM ucs WHERE Codigo=?")) {

                pstm.setString(1, key.toString());
                pstm.executeUpdate(); // Executes the delete operation
            } catch (SQLException e) {
                throw new RuntimeException("Database error while removing UC: " + e.getMessage(), e);
            }
        }
        return uc;
    }

    @Override
    public void putAll(Map<? extends String, ? extends UC> ucs) {
        for(UC uc : ucs.values()) {
            this.put(uc.getCodigo(), uc);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("DELETE FROM ucs")) {

            pstm.executeUpdate(); // Executes the delete operation
        } catch (SQLException e) {
            throw new RuntimeException("Database error while clearing UCs: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<UC> values() {
        Collection<UC> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Codigo FROM ucs")) {
            while (rs.next()) {
                String codigo = rs.getString("Codigo");
                UC uc = this.get(codigo);
                res.add(uc);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, UC>> entrySet() {
        return null;
    }
}
