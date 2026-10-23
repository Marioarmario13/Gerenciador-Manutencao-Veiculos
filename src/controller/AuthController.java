package controller;

import dao.UsuarioDAO;
import model.Usuario;
import util.AppException;
import util.Util;

/** Regras do login. */
public class AuthController {
    private final UsuarioDAO dao = new UsuarioDAO();

    /** Retorna o usuário logado ou lança AppException se o login não for válido. */
    public Usuario login(String login, String senha) {
        if (Util.vazio(login) || Util.vazio(senha)) {
            throw new AppException("Informe usuário e senha.");
        }
        Usuario usuario = dao.autenticar(login.trim(), senha);
        if (usuario == null) throw new AppException("Usuário ou senha inválidos.");
        return usuario;
    }
}
