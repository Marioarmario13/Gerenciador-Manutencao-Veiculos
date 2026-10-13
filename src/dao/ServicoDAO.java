package dao;

import model.Servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicoDAO {

    /** Critérios de ordenação disponíveis (rótulo na tela -> coluna SQL). */
    public static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Nome", "nome");
        COLUNAS.put("Preço", "preco");
        COLUNAS.put("Intervalo (km)", "intervalo_km");
        COLUNAS.put("Código", "id");
    }

    public List<Servico> listar(String busca, String ordem1, String ordem2) throws SQLException {
        String sql = "SELECT id, nome, descricao, preco, intervalo_km FROM servico "
                + "WHERE nome ILIKE ? OR descricao ILIKE ?" + Ordem.montar(COLUNAS, ordem1, ordem2);
        List<Servico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
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
        }
        return lista;
    }

    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servico (nome, descricao, preco, intervalo_km) VALUES (?, ?, ?, ?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getNome());
            ps.setString(2, s.getDescricao());
            ps.setBigDecimal(3, s.getPreco());
            ps.setInt(4, s.getIntervaloKm());
            ps.executeUpdate();
        }
    }

    public void atualizar(Servico s) throws SQLException {
        String sql = "UPDATE servico SET nome = ?, descricao = ?, preco = ?, intervalo_km = ? WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getNome());
            ps.setString(2, s.getDescricao());
            ps.setBigDecimal(3, s.getPreco());
            ps.setInt(4, s.getIntervaloKm());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM servico WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
