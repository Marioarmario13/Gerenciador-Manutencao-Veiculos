package dao;

import model.Agendamento;
import util.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** DAO da tabela agenda (ligação entre carro e serviço). */
public class AgendamentoDAO implements DAO<Agendamento> {

    // opções de ordenação: nome -> coluna no SQL
    private static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Data", "a.data_agendada");
        COLUNAS.put("Serviço", "s.nome");
        COLUNAS.put("Placa", "c.placa");
        COLUNAS.put("Status", "a.status");
        COLUNAS.put("Código", "a.id");
    }

    @Override
    public String[] colunas() {
        return COLUNAS.keySet().toArray(new String[0]);
    }

    @Override
    public List<Agendamento> listar(String busca, String ordem1, String ordem2) {
        String sql = "SELECT a.id, a.carro_id, a.servico_id, s.nome, s.preco, a.data_agendada, a.status "
                + "FROM agenda a JOIN carro c ON c.id = a.carro_id JOIN servico s ON s.id = a.servico_id "
                + "WHERE c.placa LIKE ? OR s.nome LIKE ? OR a.status LIKE ?"
                + Conexao.ordenarPor(COLUNAS, ordem1, ordem2);
        List<Agendamento> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
            for (int i = 1; i <= 3; i++) ps.setString(i, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Agendamento a = new Agendamento();
                    a.setId(rs.getInt("id"));
                    a.setCarroId(rs.getInt("carro_id"));
                    a.setServicoId(rs.getInt("servico_id"));
                    a.setServicoNome(rs.getString("nome"));
                    a.setPreco(rs.getBigDecimal("preco"));
                    a.setData(rs.getDate("data_agendada"));
                    a.setStatus(rs.getString("status"));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
        return lista;
    }

    @Override
    public void inserir(Agendamento a) {
        Conexao.executar("INSERT INTO agenda (carro_id, servico_id, data_agendada, status) VALUES (?, ?, ?, ?)",
                a.getCarroId(), a.getServicoId(), a.getData(), a.getStatus());
    }

    @Override
    public void atualizar(Agendamento a) {
        Conexao.executar("UPDATE agenda SET carro_id = ?, servico_id = ?, data_agendada = ?, status = ? WHERE id = ?",
                a.getCarroId(), a.getServicoId(), a.getData(), a.getStatus(), a.getId());
    }

    @Override
    public void excluir(int id) {
        Conexao.executar("DELETE FROM agenda WHERE id = ?", id);
    }
}
