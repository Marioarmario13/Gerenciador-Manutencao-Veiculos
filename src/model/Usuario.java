package model;

/** Usuário que pode entrar no sistema (tabela usuario). */
public class Usuario {
    private int id;
    private String login;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
}
