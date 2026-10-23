package controller;

import dao.MecanicoDAO;
import model.Mecanico;
import util.AppException;
import util.Util;

import java.util.List;

/** Regras da tela de mecânicos: valida os dados e chama o MecanicoDAO. */
public class MecanicoController {
    private final MecanicoDAO dao = new MecanicoDAO();

    public String[] colunas() {
        return MecanicoDAO.COLUNAS.keySet().toArray(new String[0]);
    }

    public List<Mecanico> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    /** id == 0 insere um novo mecânico; id > 0 atualiza o existente. */
    public void salvar(int id, String nome, String especialidade, String telefone, boolean ativo) {
        if (Util.vazio(nome)) throw new AppException("O nome do mecânico é obrigatório.");
        if (Util.vazio(telefone)) throw new AppException("O telefone é obrigatório.");
        int digitos = telefone.replaceAll("\\D", "").length();
        if (digitos != 10 && digitos != 11) {
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

    public void excluir(int id) {
        dao.excluir(id);
    }
}
