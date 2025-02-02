package data;

public class DAOconfig {

    static final String USERNAME = "root";
    static final String PASSWORD = "root";
    private static final String DATABASE = "projetodss";
    //private static final String DRIVER = "jdbc:mariadb";
    private static final String DRIVER = "jdbc:mysql";
    static final String URL = DRIVER+"://localhost:3306/"+DATABASE;
}
