package controller;

import dao.AgendamentoDAO;
import dao.CarroDAO;
import dao.MecanicoDAO;
import dao.ServicoDAO;
import model.Agendamento;
import model.Carro;
import model.Mecanico;
import model.Servico;
import util.AppException;
import util.Util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/** Regras da tela de carros: cadastro do carro e sua agenda de serviços. */
public class CarroController {
    private final CarroDAO dao = new CarroDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final AgendamentoDAO agendaDAO = new AgendamentoDAO();

    // ---------- Carros ----------

    public String[] colunas() {
        return CarroDAO.COLUNAS.keySet().toArray(new String[0]);
    }

    public List<Carro> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    public List<Mecanico> mecanicosAtivos() {
        return mecanicoDAO.listarAtivos();
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

    public void excluir(int id) {
        dao.excluir(id);
    }

    // ---------- Agenda de serviços do carro ----------

    public List<Servico> servicos() {
        return servicoDAO.listar("", "Nome", null);
    }

    public List<Agendamento> listarAgenda(int carroId) {
        return agendaDAO.listarPorCarro(carroId);
    }

    public void agendar(int carroId, Servico servico, String data) {
        if (carroId == 0) throw new AppException("Selecione um carro na tabela primeiro.");
        if (servico == null) throw new AppException("Selecione um serviço.");
        Date d = Util.parseData(data, "Data");
        if (d.toLocalDate().isBefore(LocalDate.now())) {
            throw new AppException("A data do agendamento não pode estar no passado.");
        }
        agendaDAO.inserir(carroId, servico.getId(), d);
    }

    public void mudarStatus(int agendamentoId, String status) {
        agendaDAO.atualizarStatus(agendamentoId, status);
    }

    public void removerDaAgenda(int agendamentoId) {
        agendaDAO.excluir(agendamentoId);
    }
}
