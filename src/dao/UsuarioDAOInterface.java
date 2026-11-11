package dao;

import model.Usuario;

/**
 * Interface do DAO de usuários: o contrato do login.
 * Fica separada da DAO<T> porque o usuário não é um cadastro do sistema
 * (não é listado, inserido, alterado nem excluído pelas telas): só o login é conferido.
 */
public interface UsuarioDAOInterface {

    /** Retorna o usuário com esse login e senha, ou null se não existir. */
    Usuario autenticar(String login, String senha);
}
