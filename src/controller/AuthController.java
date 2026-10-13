package controller;

import dao.UsuarioDAO;
import model.Usuario;
import util.AppException;
import util.Util;

import java.sql.SQLException;

public class AuthController {
    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario login(String login, String senha) {
        if (Util.vazio(login) || Util.vazio(senha)) {
            throw new AppException("Informe usuário e senha.");
        }
        try {
            Usuario u = dao.autenticar(login.trim(), senha);
            if (u == null) throw new AppException("Usuário ou senha inválidos.");
            return u;
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }
}
