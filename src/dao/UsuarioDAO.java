package dao;

import model.Usuario;
import util.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** DAO da tabela usuario. Não implementa DAO<T> porque o sistema não cadastra usuários, só confere o login. */
public class UsuarioDAO {

    // devolve o usuário se o login e a senha baterem, ou null
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT id, login FROM usuario WHERE login = ? AND senha = ?";
        try (Connection conexao = Conexao.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, hashSenha(senha));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setLogin(rs.getString("login"));
                return usuario;
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    // a senha nunca fica salva em texto: o banco guarda o hash SHA-256 dela
    private String hashSenha(String senha) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder();
            for (byte b : hash) hexadecimal.append(String.format("%02x", b));
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // nunca acontece: todo Java tem SHA-256
        }
    }
}
