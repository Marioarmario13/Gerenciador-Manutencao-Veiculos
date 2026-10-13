package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Usuario;

public class UsuarioDAO {

    /** Retorna o usuário se login/senha conferem, ou null. */
    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT id, login FROM usuario WHERE login = ? AND senha = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, hashSenha(senha));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setLogin(rs.getString("login"));
                return u;
            }
        }
    }

    private String hashSenha(String senha) throws SQLException {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder(hash.length * 2);
            for (byte b : hash) hexadecimal.append(String.format("%02x", b & 0xff));
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("SHA-256 não está disponível nesta instalação Java.", e);
        }
    }
}
