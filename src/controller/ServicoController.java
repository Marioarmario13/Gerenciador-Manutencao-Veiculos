package controller;

import dao.DAO;
import dao.ServicoDAO;
import model.Servico;
import util.AppException;
import util.Util;

import java.util.List;

/** Regras da tela de serviços. */
public class ServicoController {
    // declarado pela interface DAO; a classe concreta só aparece no new
    private final DAO<Servico> dao = new ServicoDAO();

    public String[] colunas() {
        return dao.colunas();
    }

    public List<Servico> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    // id 0 = serviço novo (INSERT); senão atualiza (UPDATE)
    public void salvar(int id, String nome, String descricao, String preco, String intervaloKm) {
        if (Util.vazio(nome)) throw new AppException("O nome do serviço é obrigatório.");
        Servico s = new Servico();
        s.setId(id);
        s.setNome(nome.trim());
        s.setDescricao(descricao == null ? "" : descricao.trim());
        s.setPreco(Util.parseDecimal(preco, "Preço"));
        s.setIntervaloKm(Util.parseInt(intervaloKm, "Intervalo (km)", 0, 1000000));
        if (id == 0) dao.inserir(s);
        else dao.atualizar(s);
    }

    // o banco não deixa excluir serviço que está na agenda de algum carro (RESTRICT)
    public void excluir(int id) {
        dao.excluir(id);
    }
}
