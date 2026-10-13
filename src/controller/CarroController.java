package controller;

import dao.CarroDAO;
import dao.MecanicoDAO;
import model.Carro;
import model.Mecanico;
import util.AppException;
import util.Util;

import java.sql.SQLException;
import java.time.Year;
import java.util.List;

public class CarroController {
    private final CarroDAO dao = new CarroDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();

    public String[] colunas() {
        return CarroDAO.COLUNAS.keySet().toArray(new String[0]);
    }

    public List<Carro> listar(String busca, String ordem1, String ordem2) {
        try {
            return dao.listar(busca, ordem1, ordem2);
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    public List<Mecanico> mecanicosAtivos() {
        try {
            return mecanicoDAO.listarAtivos();
        } catch (SQLException e) {
            throw new AppException(Erros.msg(e));
        }
    }

    /** mecanico pode ser null ou ter id 0 (sem mecânico atribuído). */
    public void salvar(int id, String placa, String modelo, String ano, String km, String problema, Mecanico mecanico) {
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
        try {
            if (id == 0) dao.inserir(c);
            else dao.atualizar(c);
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
