package data;

import business.SubHorarios.*;
import business.SubUtilizadores.Aluno;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class AlunoDAO implements Map<String,Aluno> {

    private static AlunoDAO singleton = null;

    private AlunoDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS alunos (" +
                    "Email varchar(45) NOT NULL PRIMARY KEY," +
                    "Nome varchar(45) NOT NULL," +
                    "Numero varchar(10) NOT NULL," +
                    "Password varchar(45) NOT NULL," +
                    "Regime varchar(10) DEFAULT NULL," +
                    "Media float NOT NULL," +
                    "Alocado boolean NOT NULL)";
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS alunoUC (" +
                    "EmailAluno varchar(45)," +
                    "CodigoUC varchar(10)," +
                    "PRIMARY KEY(EmailAluno, CodigoUC)," +
                    "foreign key(EmailAluno) references alunos(Email) ON DELETE CASCADE," +
                    "foreign key(CodigoUC) references ucs(Codigo) ON DELETE CASCADE)";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public static AlunoDAO getInstance() {
        if (AlunoDAO.singleton == null) {
            AlunoDAO.singleton = new AlunoDAO();
        }
        return AlunoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM alunos")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT Email FROM alunos WHERE Email=?")) {
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
        Aluno a = (Aluno) value;
        return this.containsKey(a.getEmail());
    }

    @Override
    public Aluno get(Object key) {
        Aluno a = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM alunos WHERE Email=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String email = rs.getString("Email");
                    String nome = rs.getString("Nome");
                    String numero = rs.getString("Numero");
                    String regime = rs.getString("Regime");
                    Float media = rs.getFloat("Media");
                    boolean alocado = rs.getBoolean("Alocado");

                    a = new Aluno(numero,nome,email,media,regime,alocado);

                    PreparedStatement psUCs = conn.prepareStatement("SELECT CodigoUC FROM alunoUC WHERE EmailAluno = ?");
                    psUCs.setString(1, email);
                    ResultSet rsUCs = psUCs.executeQuery();

                    UCDAO ucs = UCDAO.getInstance();
                    while (rsUCs.next()) {
                        a.addUC(ucs.get(rsUCs.getString("CodigoUC")));
                    }

                    return a;
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return a;
    }

    @Override
    public Aluno put(String key, Aluno value) {
        Aluno a = get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)){
            conn.setAutoCommit(false);
            try {
                PreparedStatement psAluno = conn.prepareStatement(
                        "INSERT INTO alunos (Email,Nome,Numero,Password,Regime,Media,Alocado) VALUES (?,?,?,?,?,?,?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "Nome= VALUES(Nome), " +
                                "Numero= VALUES(Numero), " +
                                "Password= VALUES(Password), " +
                                "Regime= VALUES(Regime), " +
                                "Media= VALUES(Media), " +
                                "Alocado= VALUES(Alocado)");

                psAluno.setString(1, value.getEmail());
                psAluno.setString(2, value.getNome());
                psAluno.setString(3, value.getNumero());
                psAluno.setString(4, value.getPassword());
                psAluno.setString(5, value.RegimeToString());
                psAluno.setFloat(6, value.getMedia());
                psAluno.setBoolean(7,value.isAlocado());
                psAluno.executeUpdate();

                PreparedStatement psAlunoUC = conn.prepareStatement(
                        "INSERT IGNORE INTO alunoUC (EmailAluno,CodigoUC) VALUES (?, ?)");

                List<UC> ucsAluno = value.getUcs();
                for(UC uc : ucsAluno){
                    psAlunoUC.setString(1, value.getEmail());
                    psAlunoUC.setString(2, uc.getCodigo());
                    psAlunoUC.executeUpdate();
                }


                conn.commit();
                return a;
            }catch (SQLException e) {
                conn.rollback();
                throw e;
            }finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while saving UC: " + e.getMessage(), e);
        }
    }

    @Override
    public Aluno remove(Object key) {
        Aluno a = get(key);
        if (a != null) {
            try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {
                PreparedStatement pstm = conn.prepareStatement("DELETE FROM alunos WHERE Email=?");

                pstm.setString(1, (String) key);
                pstm.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Database error while removing UC: " + e.getMessage(), e);
            }
        }
        return a;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Aluno> alunos) {
        for(Aluno a : alunos.values()) {
            this.put(a.getEmail(), a);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)){
            conn.setAutoCommit(false);
            try{
                Statement stmAlunoUC = conn.createStatement();
                stmAlunoUC.executeUpdate("DELETE FROM alunoUC");

                Statement stmAluno = conn.createStatement();
                stmAluno.executeUpdate("DELETE FROM alunos");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while clearing UCs: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Aluno> values() {
        Collection<Aluno> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)){
             Statement stm = conn.createStatement();

             try(ResultSet rs = stm.executeQuery("SELECT * FROM alunos")) {
                 while (rs.next()) {
                     String email = rs.getString("Email");
                     Aluno a = this.get(email);

                     res.add(a);
                 }
             }

        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Aluno>> entrySet() {
        return null;
    }
}
