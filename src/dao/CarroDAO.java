package dao;

import model.Carro;
import util.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Acesso à tabela carro (listar, inserir, atualizar e excluir). */
public class CarroDAO {

    /** Critérios de ordenação disponíveis (rótulo na tela -> coluna SQL). */
    public static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Placa", "c.placa");
        COLUNAS.put("Modelo", "c.modelo");
        COLUNAS.put("Ano", "c.ano");
        COLUNAS.put("Km", "c.km");
        COLUNAS.put("Mecânico", "m.nome");
        COLUNAS.put("Código", "c.id");
    }

    public List<Carro> listar(String busca, String ordem1, String ordem2) {
        String sql = "SELECT c.id, c.placa, c.modelo, c.ano, c.km, c.problema, "
                + "c.mecanico_id, m.nome AS mecanico_nome FROM carro c "
                + "LEFT JOIN mecanico m ON m.id = c.mecanico_id "
                + "WHERE c.placa LIKE ? OR c.modelo LIKE ? OR CAST(c.ano AS CHAR) LIKE ? "
                + "OR CAST(c.km AS CHAR) LIKE ? OR c.problema LIKE ? "
                + "OR IFNULL(m.nome, '-') LIKE ? "
                + "OR CAST(c.id AS CHAR) LIKE ?"
                + Conexao.ordenarPor(COLUNAS, ordem1, ordem2);
        List<Carro> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            // a busca também aceita placa com hífen digitado pelo usuário
            String filtro = "%" + (busca == null ? "" : busca.trim().replace("-", "")) + "%";
            for (int i = 1; i <= 7; i++) ps.setString(i, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Carro car = new Carro();
                    car.setId(rs.getInt("id"));
                    car.setPlaca(rs.getString("placa"));
                    car.setModelo(rs.getString("modelo"));
                    car.setAno(rs.getInt("ano"));
                    car.setKm(rs.getInt("km"));
                    car.setProblema(rs.getString("problema"));
                    car.setMecanicoId(rs.getInt("mecanico_id"));
                    car.setMecanicoNome(rs.getString("mecanico_nome"));
                    lista.add(car);
                }
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
        return lista;
    }

    public void inserir(Carro car) {
        Conexao.executar("INSERT INTO carro (placa, modelo, ano, km, problema, mecanico_id) VALUES (?, ?, ?, ?, ?, ?)",
                car.getPlaca(), car.getModelo(), car.getAno(), car.getKm(), car.getProblema(), mecanico(car));
    }

    public void atualizar(Carro car) {
        Conexao.executar("UPDATE carro SET placa = ?, modelo = ?, ano = ?, km = ?, problema = ?, mecanico_id = ? WHERE id = ?",
                car.getPlaca(), car.getModelo(), car.getAno(), car.getKm(), car.getProblema(), mecanico(car),
                car.getId());
    }

    /** Sem mecânico (id 0) é gravado como NULL no banco. */
    private Integer mecanico(Carro car) {
        return car.getMecanicoId() == 0 ? null : car.getMecanicoId();
    }

    /** Remove o carro e sua agenda (ON DELETE CASCADE). */
    public void excluir(int id) {
        Conexao.executar("DELETE FROM carro WHERE id = ?", id);
    }
}
