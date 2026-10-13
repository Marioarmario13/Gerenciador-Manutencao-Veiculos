package controller;

import dao.MecanicoDAO;
import model.Mecanico;
import util.AppException;
import util.Util;

import java.sql.SQLException;
import java.util.List;

public class MecanicoController {
    private final MecanicoDAO dao = new MecanicoDAO();

    public String[] colunas() {
        return MecanicoDAO.COLUNAS.keySet().toArray(new String[0]);
    }

    public List<Mecanico> listar(String busca, String ordem1, String ordem2) {
        try {
            return dao.listar(busca, ordem1, ordem2);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public void salvar(int id, String nome, String especialidade, String telefone, boolean ativo) {
        if (Util.vazio(nome)) throw new AppException("O nome do mecânico é obrigatório.");
        if (Util.vazio(telefone)) throw new AppException("O telefone é obrigatório.");
        int digitos = telefone.replaceAll("\\D", "").length();
        if (digitos != 10 && digitos != 11) {
            throw new AppException("Telefone inválido. Informe DDD + número (10 ou 11 dígitos).");
        }
        Mecanico m = new Mecanico();
        m.setId(id);
        m.setNome(nome.trim());
        m.setEspecialidade(especialidade == null ? "" : especialidade.trim());
        m.setTelefone(telefone.trim());
        m.setAtivo(ativo);
        try {
            if (id == 0) dao.inserir(m);
            else dao.atualizar(m);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public void excluir(int id) {
        try {
            dao.excluir(id);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }
}
