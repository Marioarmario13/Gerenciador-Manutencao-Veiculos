package controller;

import dao.DAO;
import dao.MecanicoDAO;
import model.Mecanico;
import util.AppException;
import util.Util;

import java.util.List;

/** Regras da tela de mecânicos. */
public class MecanicoController {
    // declarado pela interface DAO; a classe concreta só aparece no new
    private final DAO<Mecanico> dao = new MecanicoDAO();

    public String[] colunas() {
        return dao.colunas();
    }

    public List<Mecanico> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    // id 0 = mecânico novo (INSERT); senão atualiza (UPDATE)
    public void salvar(int id, String nome, String especialidade, String telefone, boolean ativo) {
        if (Util.vazio(nome)) throw new AppException("O nome do mecânico é obrigatório.");
        if (Util.vazio(telefone)) throw new AppException("O telefone é obrigatório.");
        int digitos = telefone.replaceAll("\\D", "").length();
        // só aceita números, espaço, parênteses e hífen, com 10 ou 11 dígitos
        if (!telefone.matches("[0-9()\\-\\s]+") || (digitos != 10 && digitos != 11)) {
            throw new AppException("Telefone inválido. Informe DDD + número (10 ou 11 dígitos).");
        }

        Mecanico mecanico = new Mecanico();
        mecanico.setId(id);
        mecanico.setNome(nome.trim());
        mecanico.setEspecialidade(especialidade == null ? "" : especialidade.trim());
        mecanico.setTelefone(telefone.trim());
        mecanico.setAtivo(ativo);
        if (id == 0) dao.inserir(mecanico);
        else dao.atualizar(mecanico);
    }

    // o banco não deixa excluir mecânico que está em algum carro (RESTRICT)
    public void excluir(int id) {
        dao.excluir(id);
    }
}
