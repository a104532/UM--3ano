package uminho.dss.turmas3l.data;

import uminho.dss.turmas3l.business.Aluno;
import uminho.dss.turmas3l.business.Sala;
import uminho.dss.turmas3l.business.Turma;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;

public class AlunoDAO implements Map<String, Aluno> {
    private static AlunoDAO singleton = null;

    //tem de ser privado porque não pode ser criado um novo
    private AlunoDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS alunos (" +
                    "Num varchar(10) NOT NULL PRIMARY KEY," +
                    "Nome varchar(45) DEFAULT NULL," +
                    "Email varchar(45) DEFAULT NULL," +
                    "Turma varchar(10), foreign key(Turma) references turmas(Id))";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */

    //vai devolver a instância existente (apenas 1)
    public static AlunoDAO getInstance() {
        if (AlunoDAO.singleton == null) { //se não for null é porque a instância já foi criada
            AlunoDAO.singleton = new AlunoDAO();
        }
        return AlunoDAO.singleton;
    }

    /**
     * @return número de turmas na base de dados
     */
    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM turmas")) {
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

    /**
     * Método que verifica se existem turmas
     *
     * @return true se existirem 0 turmas
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Método que cerifica se um id de turma existe na base de dados
     *
     * @param key id da turma
     * @return true se a turma existe
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT Id FROM alunos WHERE Id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                r = rs.next();  // A chave existe na tabela
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se uma turma existe na base de dados
     *
     * Esta implementação é provisória. Devia testar o objecto e não apenas a chave.
     *
     * @param value ...
     * @return ...
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public boolean containsValue(Object value) {
        Aluno a = (Aluno) value;
        return this.containsKey(a.getNumero());
    }

    /**
     * Obter uma turma, dado o seu id
     *
     * @param key id da turma
     * @return a turma caso exista (null noutro caso)
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public Aluno get(Object key) {
        Aluno a = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM alunos WHERE Num=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String id = rs.getString("Id");  // Podíamos usar a key, mas assim temos a certeza que é o id da BD
                    // Reconstruir a colecção de alunos da turma
                    Collection<String> alunos = getAlunosTurma(key.toString(), pstm);

                    // Reconstruir a Sala
                    Sala s = null;
                    String sql = "SELECT * FROM salas WHERE Num='"+rs.getString("Sala")+"'";
                    try (ResultSet rsa = pstm.executeQuery(sql)) {  // Nota: abrir um novo ResultSet no mesmo Statement fecha o ResultSet anterior
                        if (rsa.next()) {  // Encontrou a sala
                            s = new Sala(rs.getString("Sala"),
                                    rsa.getString("Edificio"),
                                    rsa.getInt("Capacidade"));
                        } else {
                            // BD inconsistente!! Sala não existe - tratar com excepções.
                        } // catch é feito no try inicial - este try serve para fechar o ResultSet automaticamente

                    }

                    // Reconstruir a turma cokm os dados obtidos da BD
                    t = new Turma(id, s, alunos);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public Aluno put(String key, Aluno value) {
            Aluno res = null;
            Sala s = t.getSala();
            try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
                 Statement stm = conn.createStatement()) {

                // Actualizar a lista de alunos
                stm.executeUpdate(
                        "INSERT INTO alunos VALUES ('"+a.getId()+"', '"+s.getNumero()+"') " +
                                "ON DUPLICATE KEY UPDATE Sala=VALUES(Sala)");

            } catch (SQLException e) {
                // Database error!
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
            return res;

    }

    /**
     * Método auxiliar que obtem a lista de alunos da turma
     *
     * @param tid o id da turma
     * @return a lista de alunos da turma
     */
    private Collection<String> getAlunosTurma(String tid, Statement stm) throws SQLException {
        Collection<String> alunos = new TreeSet<>();
        try (ResultSet rsa = stm.executeQuery("SELECT Num FROM alunos WHERE Turma='"+tid+"'")) {
            while(rsa.next()) {
                alunos.add(rsa.getString("Num"));
            }
        } // execepção é enviada a quem chama o método - este try serve para fechar o ResultSet automaticamente
        return alunos;
    }

    /**
     * Insere uma turma na base de dados
     *
     * ATENÇÂO: Esta implementação é provisória.
     * Falta devolver o valor existente (caso exista um)
     * Falta remover a sala anterior, caso esteja a ser alterada
     * Deveria utilizar transacções...
     *
     * @param key o id da turma
     * @param t a turma
     * @return para já retorna sempre null (deverá devolver o valor existente, caso exista um)
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public Turma put(String key, Turma t) {
        Turma res = null;
        Sala s = t.getSala();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            // Actualizar a Sala
            stm.executeUpdate(
                    "INSERT INTO salas " +
                            "VALUES ('"+ s.getNumero()+ "', '"+
                            s.getEdificio()+"', "+
                            s.getCapacidade()+") " +
                            "ON DUPLICATE KEY UPDATE Edificio=Values(Edificio), " +
                            "Capacidade=Values(Capacidade)");

            // Actualizar a turma
            stm.executeUpdate(
                    "INSERT INTO turmas VALUES ('"+t.getId()+"', '"+s.getNumero()+"') " +
                            "ON DUPLICATE KEY UPDATE Sala=VALUES(Sala)");

            // Actualizar os alunos da turma
            Collection<String> oldAl = getAlunosTurma(key, stm);
            Collection<String> newAl = t.getAlunos().stream().collect(toList());
            newAl.removeAll(oldAl);         // Alunos que entram na turma, em relação ao que está na BD
            oldAl.removeAll(t.getAlunos().stream().collect(toList())); // Alunos que saem na turma, em relação ao que está na BD
            try (PreparedStatement pstm = conn.prepareStatement("UPDATE alunos SET Turma=? WHERE Num=?")) {
                // Remover os que saem da turma (colocar a NULL a coluna que diz qual a turma dos alunos)
                pstm.setNull(1, Types.VARCHAR);
                for (String a: oldAl) {
                    pstm.setString(2, a);
                    pstm.executeUpdate();
                }
                // Adicionar os que entram na turma (colocar o Id da turma na coluna Turma da tabela alunos)
                // ATENÇÃO: Para já isto não vai funcionar pois os alunos não estão na tabela
                //          (não há lá nada para atualizar).  Funcionará quando tivermos um AlunoDAO
                //          a guardar os alunos na tabela 'alunos'.
                pstm.setString(1, t.getId());
                for (String a: newAl) {
                    pstm.setString(2, a);
                    pstm.executeUpdate();
                }
            }

        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    /**
     * Remover uma turma, dado o seu id
     *
     * NOTA: Não estamos a apagar a sala...
     *
     * @param key id da turma a remover
     * @return a turma removida
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public Turma remove(Object key) {
        Turma t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement turmas_pstm = conn.prepareStatement("DELETE FROM turmas WHERE Id=?");
             PreparedStatement alunos_pstm = conn.prepareStatement("UPDATE alunos SET Turma=? WHERE Num=?")) {
            // retirar os alunos da turma
            alunos_pstm.setNull(1, Types.VARCHAR);
            for (String na: t.getAlunos()) {
                alunos_pstm.setString(2, na);
                alunos_pstm.executeUpdate();
            }
            // apagar a turma
            turmas_pstm.setString(1, key.toString());
            turmas_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Aluno> m) {

    }

    /**
     * Adicionar um conjunto de turmas à base de dados
     *
     * @param turmas as turmas a adicionar
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public void putAll(Map<? extends String, ? extends Turma> turmas) {
        for(Turma t : turmas.values()) {
            this.put(t.getId(), t);
        }
    }

    /**
     * Apagar todas as turmas
     *
     * @throws NullPointerException Em caso de erro - deveriam ser criadas exepções do projecto
     */
    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("UPDATE alunos SET Turma=NULL");
            stm.executeUpdate("TRUNCATE turmas");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * NÃO IMPLEMENTADO!
     * @return ainda nada!
     */
    @Override
    public Set<String> keySet() {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * @return Todos as turmas da base de dados
     */
    @Override
    public Collection<Turma> values() {
        Collection<Turma> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Id FROM turmas")) { // ResultSet com os ids de todas as turmas
            while (rs.next()) {
                String idt = rs.getString("Id"); // Obtemos um id de turma do ResultSet
                Turma t = this.get(idt);                    // Utilizamos o get para construir as turmas uma a uma
                res.add(t);                                 // Adiciona a turma ao resultado.
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    /**
     * NÃO IMPLEMENTADO!
     * @return ainda nada!
     */
    @Override
    public Set<Entry<String, Turma>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Aluno>> entrySet() not implemented!");
    }
}

