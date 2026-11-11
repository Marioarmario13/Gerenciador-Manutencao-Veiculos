package controller;

import dao.UsuarioDAO;
import model.Usuario;
import util.AppException;
import util.Util;

/** Regras do login. */
public class AuthController {
    // O UsuarioDAO não usa a interface DAO<T> porque o login não é um cadastro: só confere usuário e senha.
    private final UsuarioDAO dao = new UsuarioDAO();

    // se faltar dado ou a senha estiver errada, lança AppException com a mensagem
    public Usuario login(String login, String senha) {
        if (Util.vazio(login) || Util.vazio(senha)) {
            throw new AppException("Informe usuário e senha.");
        }
        Usuario usuario = dao.autenticar(login.trim(), senha);
        if (usuario == null) throw new AppException("Usuário ou senha inválidos.");
        return usuario;
    }
}
