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

/** Regras da tela de carros: cadastro do carro e agenda de serviços. */
public class CarroController {
    // Os DAOs são declarados pelo tipo da interface (DAO<...>). O controller só conhece listar,
    // inserir, atualizar e excluir, e não depende do SQL de cada classe. A classe concreta só
    // aparece no new (polimorfismo).
    private final DAO<Carro> dao = new CarroDAO();
    private final DAO<Mecanico> mecanicoDAO = new MecanicoDAO();
    private final DAO<Servico> servicoDAO = new ServicoDAO();
    private final DAO<Agendamento> agendaDAO = new AgendamentoDAO();

    // carros

    public String[] colunas() {
        return dao.colunas();
    }

    public List<Carro> listar(String busca, String ordem1, String ordem2) {
        return dao.listar(busca, ordem1, ordem2);
    }

    // só os mecânicos ativos aparecem no combo da tela de carros
    public List<Mecanico> mecanicosAtivos() {
        List<Mecanico> ativos = new ArrayList<>();
        for (Mecanico m : mecanicoDAO.listar("", "Nome", null)) {
            if (m.isAtivo()) ativos.add(m);
        }
        return ativos;
    }

    // id 0 = carro novo (INSERT); id maior que 0 = carro que já existe (UPDATE)
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

    // a agenda do carro é apagada junto pelo banco (ON DELETE CASCADE)
    public void excluir(int id) {
        dao.excluir(id);
    }

    // agenda do carro

    public List<Servico> servicos() {
        return servicoDAO.listar("", "Nome", null);
    }

    // busca a agenda pela interface e separa só a deste carro
    public List<Agendamento> listarAgenda(int carroId) {
        List<Agendamento> doCarro = new ArrayList<>();
        for (Agendamento a : agendaDAO.listar("", "Data", "Serviço")) {
            if (a.getCarroId() == carroId) doCarro.add(a);
        }
        return doCarro;
    }

    // Regra de negócio fica no controller (MVC): soma só o que ainda está agendado.
    // A tela só mostra o valor.
    public BigDecimal totalPrevisto(List<Agendamento> agenda) {
        BigDecimal total = BigDecimal.ZERO;
        for (Agendamento a : agenda) {
            if (Agendamento.AGENDADO.equals(a.getStatus())) total = total.add(a.getPreco());
        }
        return total;
    }

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

    // concluir ou cancelar um serviço da agenda
    public void mudarStatus(Agendamento agendamento, String status) {
        agendamento.setStatus(status);
        agendaDAO.atualizar(agendamento);
    }

    public void removerDaAgenda(int agendamentoId) {
        agendaDAO.excluir(agendamentoId);
    }
}
