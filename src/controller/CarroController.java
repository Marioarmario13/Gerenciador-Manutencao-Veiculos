package controller;

import dao.AgendamentoDAO;
import dao.CarroDAO;
import dao.DAO;
import dao.MecanicoDAO;
import dao.ServicoDAO;
import model.Agendamento;
import model.Carro;
import model.Mecanico;
import model.Servico;
import util.AppException;
import util.Util;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/** Regras da tela de carros: cadastro do carro e sua agenda de serviços. */
public class CarroController {
    // o controller conhece só a interface DAO; a classe concreta aparece apenas no "new"
    private final DAO<Carro> dao = new CarroDAO();
    private final DAO<Mecanico> mecanicoDAO = new MecanicoDAO();
    private final DAO<Servico> servicoDAO = new ServicoDAO();
    private final DAO<Agendamento> agendaDAO = new AgendamentoDAO();

    // ---------- Carros ----------

    /** Colunas que a tela oferece para ordenar a lista de carros. */
    public String[] colunas() {
        return dao.colunas();
    }

    /** Lista os carros que contêm a busca, ordenados pelos critérios escolhidos. */
    public List<Carro> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    /** Mecânicos em atividade, em ordem de nome (para o combo "Mecânico"). */
    public List<Mecanico> mecanicosAtivos() {
        List<Mecanico> ativos = new ArrayList<>();
        for (Mecanico m : mecanicoDAO.listar("", "Nome", null)) {
            if (m.isAtivo()) ativos.add(m);
        }
        return ativos;
    }

    /** id == 0 insere um novo carro; id > 0 atualiza o existente. */
    public void salvar(int id, String placa, String modelo, String ano, String km, String problema,
                       Mecanico mecanico) {
        if (Util.vazio(placa)) throw new AppException("A placa é obrigatória.");
        String p = placa.trim().toUpperCase().replace("-", "");
        if (!p.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
            throw new AppException("Placa inválida. Exemplos: ABC1234 ou ABC1D23.");
        }
        if (Util.vazio(modelo)) throw new AppException("O modelo é obrigatório.");

        Carro c = new Carro();
        c.setId(id);
        c.setPlaca(p);
        c.setModelo(modelo.trim());
        c.setAno(Util.parseInt(ano, "Ano", 1900, Year.now().getValue() + 1));
        c.setKm(Util.parseInt(km, "Km", 0, 9999999));
        c.setProblema(problema == null ? "" : problema.trim());
        c.setMecanicoId(mecanico == null ? 0 : mecanico.getId());
        if (id == 0) dao.inserir(c);
        else dao.atualizar(c);
    }

    /** Exclui o carro; a agenda dele é apagada junto (CASCADE no banco). */
    public void excluir(int id) {
        dao.excluir(id);
    }

    // ---------- Agenda de serviços do carro ----------

    /** Serviços em ordem de nome, para o combo da agenda. */
    public List<Servico> servicos() {
        return servicoDAO.listar("", "Nome", null);
    }

    /** Agenda de um carro, em ordem de data e depois de serviço. */
    public List<Agendamento> listarAgenda(int carroId) {
        List<Agendamento> doCarro = new ArrayList<>();
        for (Agendamento a : agendaDAO.listar("", "Data", "Serviço")) {
            if (a.getCarroId() == carroId) doCarro.add(a);
        }
        return doCarro;
    }

    /** Soma o preço dos serviços ainda agendados (os concluídos e cancelados não entram). */
    public BigDecimal totalPrevisto(List<Agendamento> agenda) {
        BigDecimal total = BigDecimal.ZERO;
        for (Agendamento a : agenda) {
            if (Agendamento.AGENDADO.equals(a.getStatus())) total = total.add(a.getPreco());
        }
        return total;
    }

    /** Valida o serviço e a data (que não pode estar no passado) e grava o agendamento. */
    public void agendar(int carroId, Servico servico, String data) {
        if (carroId == 0) throw new AppException("Selecione um carro na tabela primeiro.");
        if (servico == null) throw new AppException("Selecione um serviço.");
        Date d = Util.parseData(data, "Data");
        if (d.toLocalDate().isBefore(LocalDate.now())) {
            throw new AppException("A data do agendamento não pode estar no passado.");
        }
        Agendamento a = new Agendamento();
        a.setCarroId(carroId);
        a.setServicoId(servico.getId());
        a.setData(d);
        a.setStatus(Agendamento.AGENDADO);
        agendaDAO.inserir(a);
    }

    /** Marca o agendamento como concluído ou cancelado. */
    public void mudarStatus(Agendamento agendamento, String status) {
        agendamento.setStatus(status);
        agendaDAO.atualizar(agendamento);
    }

    /** Remove um serviço da agenda do carro. */
    public void removerDaAgenda(int agendamentoId) {
        agendaDAO.excluir(agendamentoId);
    }
}
