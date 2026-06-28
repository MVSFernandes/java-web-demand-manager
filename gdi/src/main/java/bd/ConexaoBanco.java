package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoBanco {

    private static final String URL = System.getProperty("DB_URL",
        "jdbc:mysql://localhost:3306/gdi_db?useSSL=false&allowPublicKeyRetrieval=true"
        + "&serverTimezone=America/Sao_Paulo&autoReconnect=true"
        + "&useUnicode=true&characterEncoding=UTF-8");
    private static final String USER = System.getProperty("DB_USER", "root");
    private static final String PASS = System.getProperty("DB_PASS", "root");

    private ConexaoBanco() { }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC não encontrado.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar: " + e.getMessage(), e);
        }
    }
}