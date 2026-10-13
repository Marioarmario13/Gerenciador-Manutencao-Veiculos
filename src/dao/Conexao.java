package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Cria conexões JDBC com o PostgreSQL. Lê config.properties (se existir). */
public class Conexao {

    private static String url = "jdbc:postgresql://localhost:5432/manutencao_veiculos";
    private static String usuario = "postgres";
    private static String senha = "";

    static {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            p.load(in);
            url = p.getProperty("db.url", url);
            usuario = p.getProperty("db.user", usuario);
            senha = p.getProperty("db.password", senha);
        } catch (IOException e) {
            // sem arquivo: usa os valores padrão acima
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
