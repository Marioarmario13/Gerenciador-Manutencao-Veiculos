package dao;

import model.Servico;
import util.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Acesso à tabela servico (listar, inserir, atualizar e excluir). */
public class ServicoDAO {

    /** Critérios de ordenação disponíveis (rótulo na tela -> coluna SQL). */
    public static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Nome", "nome");
        COLUNAS.put("Preço", "preco");
        COLUNAS.put("Intervalo (km)", "intervalo_km");
        COLUNAS.put("Código", "id");
    }

    public List<Servico> listar(String busca, String ordem1, String ordem2) {
        String sql = "SELECT id, nome, descricao, preco, intervalo_km FROM servico "
                + "WHERE nome LIKE ? OR descricao LIKE ? OR CAST(preco AS CHAR) LIKE ? "
                + "OR REPLACE(CAST(preco AS CHAR), '.', ',') LIKE ? "
                + "OR CAST(intervalo_km AS CHAR) LIKE ? OR CAST(id AS CHAR) LIKE ?"
                + Conexao.ordenarPor(COLUNAS, ordem1, ordem2);
        List<Servico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
            for (int i = 1; i <= 6; i++) ps.setString(i, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setId(rs.getInt("id"));
                    s.setNome(rs.getString("nome"));
                    s.setDescricao(rs.getString("descricao"));
                    s.setPreco(rs.getBigDecimal("preco"));
                    s.setIntervaloKm(rs.getInt("intervalo_km"));
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
        return lista;
    }

    public void inserir(Servico s) {
        Conexao.executar("INSERT INTO servico (nome, descricao, preco, intervalo_km) VALUES (?, ?, ?, ?)",
                s.getNome(), s.getDescricao(), s.getPreco(), s.getIntervaloKm());
    }

    public void atualizar(Servico s) {
        Conexao.executar("UPDATE servico SET nome = ?, descricao = ?, preco = ?, intervalo_km = ? WHERE id = ?",
                s.getNome(), s.getDescricao(), s.getPreco(), s.getIntervaloKm(), s.getId());
    }

    public void excluir(int id) {
        Conexao.executar("DELETE FROM servico WHERE id = ?", id);
    }
}
