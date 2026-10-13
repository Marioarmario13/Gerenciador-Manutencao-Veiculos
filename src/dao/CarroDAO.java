package dao;

import model.Carro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CarroDAO {

    public static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Placa", "c.placa");
        COLUNAS.put("Modelo", "c.modelo");
        COLUNAS.put("Ano", "c.ano");
        COLUNAS.put("Km", "c.km");
        COLUNAS.put("Mecânico", "m.nome");
        COLUNAS.put("Código", "c.id");
    }

    public List<Carro> listar(String busca, String ordem1, String ordem2) throws SQLException {
        String sql = "SELECT c.id, c.placa, c.modelo, c.ano, c.km, c.problema, c.mecanico_id, m.nome AS mecanico_nome "
                + "FROM carro c LEFT JOIN mecanico m ON m.id = c.mecanico_id "
                + "WHERE c.placa ILIKE ? OR c.modelo ILIKE ? OR c.problema ILIKE ? OR m.nome ILIKE ?"
                + Ordem.montar(COLUNAS, ordem1, ordem2);
        List<Carro> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String filtro = "%" + (busca == null ? "" : busca.trim().replace("-", "")) + "%";
            // a busca por texto livre também aceita placa com hífen digitado pelo usuário
            for (int i = 1; i <= 4; i++) ps.setString(i, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Carro car = new Carro();
                    car.setId(rs.getInt("id"));
                    car.setPlaca(rs.getString("placa"));
                    car.setModelo(rs.getString("modelo"));
                    car.setAno(rs.getInt("ano"));
                    car.setKm(rs.getInt("km"));
                    car.setProblema(rs.getString("problema"));
                    car.setMecanicoId(rs.getInt("mecanico_id"));       // 0 quando nulo
                    car.setMecanicoNome(rs.getString("mecanico_nome"));
                    lista.add(car);
                }
            }
        }
        return lista;
    }

    public void inserir(Carro car) throws SQLException {
        String sql = "INSERT INTO carro (placa, modelo, ano, km, problema, mecanico_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, car);
            ps.executeUpdate();
        }
    }

    public void atualizar(Carro car) throws SQLException {
        String sql = "UPDATE carro SET placa = ?, modelo = ?, ano = ?, km = ?, problema = ?, mecanico_id = ? WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, car);
            ps.setInt(7, car.getId());
            ps.executeUpdate();
        }
    }

    private void preencher(PreparedStatement ps, Carro car) throws SQLException {
        ps.setString(1, car.getPlaca());
        ps.setString(2, car.getModelo());
        ps.setInt(3, car.getAno());
        ps.setInt(4, car.getKm());
        ps.setString(5, car.getProblema());
        if (car.getMecanicoId() == 0) ps.setNull(6, Types.INTEGER);
        else ps.setInt(6, car.getMecanicoId());
    }

    /** A agenda do carro é apagada junto (ON DELETE CASCADE). */
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM carro WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
