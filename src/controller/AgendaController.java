package controller;

import dao.AgendamentoDAO;
import dao.ServicoDAO;
import model.Agendamento;
import model.Servico;
import util.AppException;
import util.Util;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Regras da agenda de manutenção (relação carro x serviço). */
public class AgendaController {
    private final AgendamentoDAO dao = new AgendamentoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();

    public List<Servico> servicos() {
        try {
            return servicoDAO.listar("", "Nome", null);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public List<Agendamento> listar(int carroId) {
        try {
            return dao.listarPorCarro(carroId);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public void agendar(int carroId, Servico servico, String data) {
        if (carroId == 0) throw new AppException("Selecione um carro na tabela primeiro.");
        if (servico == null) throw new AppException("Selecione um serviço.");
        Date d = Util.parseData(data, "Data");
        if (d.toLocalDate().isBefore(LocalDate.now())) {
            throw new AppException("A data do agendamento não pode estar no passado.");
        }
        try {
            dao.inserir(carroId, servico.getId(), d);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public void mudarStatus(int agendamentoId, String status) {
        try {
            dao.atualizarStatus(agendamentoId, status);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public void remover(int agendamentoId) {
        try {
            dao.excluir(agendamentoId);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }
}
