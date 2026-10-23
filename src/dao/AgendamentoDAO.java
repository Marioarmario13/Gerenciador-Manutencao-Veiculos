package dao;

import model.Agendamento;
import util.AppException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela de relação agenda (carro x serviço). */
public class AgendamentoDAO {

    public List<Agendamento> listarPorCarro(int carroId) {
        String sql = "SELECT a.id, a.carro_id, a.servico_id, s.nome, s.preco, a.data_agendada, a.status "
                + "FROM agenda a JOIN servico s ON s.id = a.servico_id "
                + "WHERE a.carro_id = ? ORDER BY a.data_agendada, s.nome";
        List<Agendamento> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, carroId);
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

    public void inserir(int carroId, int servicoId, Date data) {
        Conexao.executar("INSERT INTO agenda (carro_id, servico_id, data_agendada, status) "
                + "VALUES (?, ?, ?, 'AGENDADO')", carroId, servicoId, data);
    }

    public void atualizarStatus(int id, String status) {
        Conexao.executar("UPDATE agenda SET status = ? WHERE id = ?", status, id);
    }

    public void excluir(int id) {
        Conexao.executar("DELETE FROM agenda WHERE id = ?", id);
    }
}
