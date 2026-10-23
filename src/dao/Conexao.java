package dao;

import util.AppException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** Cria conexões JDBC com o MySQL. Lê config.properties (se existir). */
public class Conexao {

    private static String url = "jdbc:mysql://localhost:3306/manutencao_veiculos";
    private static String usuario = "root";
    private static String senha = "";

    static {
        Properties p = new Properties();
        File arquivo = new File("config.properties");
        if (arquivo.exists()) {
            try (FileInputStream in = new FileInputStream(arquivo)) {
                p.load(in);
                url = p.getProperty("db.url", url);
                usuario = p.getProperty("db.user", usuario);
                senha = p.getProperty("db.password", senha);
            } catch (IOException e) {
                throw new ExceptionInInitializerError(
                        "Não foi possível ler config.properties: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    /**
     * Executa um INSERT, UPDATE ou DELETE. Cada valor ocupa um "?" do SQL, na ordem.
     * Exemplo: executar("DELETE FROM carro WHERE id = ?", 5);
     */
    static void executar(String sql, Object... valores) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) ps.setObject(i + 1, valores[i]);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    /**
     * Monta o ORDER BY com até dois critérios escolhidos na tela.
     * Só aceita colunas do mapa (rótulo na tela -> coluna SQL), o que evita SQL injection.
     */
    static String ordenarPor(Map<String, String> colunasPermitidas, String criterio1, String criterio2) {
        List<String> colunas = new ArrayList<>();
        for (String criterio : new String[]{criterio1, criterio2}) {
            String coluna = colunasPermitidas.get(criterio);
            if (coluna != null && !colunas.contains(coluna)) colunas.add(coluna);
        }
        return colunas.isEmpty() ? "" : " ORDER BY " + String.join(", ", colunas);
    }
}
