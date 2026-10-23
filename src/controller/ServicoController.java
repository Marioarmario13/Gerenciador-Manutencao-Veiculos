package controller;

import dao.ServicoDAO;
import model.Servico;
import util.AppException;
import util.Util;

import java.util.List;

/** Regras da tela de serviços: valida os dados e chama o ServicoDAO. */
public class ServicoController {
    private final ServicoDAO dao = new ServicoDAO();

    public String[] colunas() {
        return ServicoDAO.COLUNAS.keySet().toArray(new String[0]);
    }

    public List<Servico> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    /** id == 0 insere um novo serviço; id > 0 atualiza o existente. */
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

    public void excluir(int id) {
        dao.excluir(id);
    }
}
