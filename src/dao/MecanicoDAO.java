package dao;

import model.Mecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MecanicoDAO {

    public static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Nome", "nome");
        COLUNAS.put("Especialidade", "especialidade");
        COLUNAS.put("Em atividade", "ativo");
        COLUNAS.put("Código", "id");
    }

    public List<Mecanico> listar(String busca, String ordem1, String ordem2) throws SQLException {
        String sql = "SELECT id, nome, especialidade, telefone, ativo FROM mecanico "
                + "WHERE nome ILIKE ? OR especialidade ILIKE ? OR telefone ILIKE ?"
                + Ordem.montar(COLUNAS, ordem1, ordem2);
        List<Mecanico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(ler(rs));
            }
        }
        return lista;
    }

    /** Somente mecânicos que estão trabalhando (usado na tela de carros). */
    public List<Mecanico> listarAtivos() throws SQLException {
        String sql = "SELECT id, nome, especialidade, telefone, ativo FROM mecanico WHERE ativo = TRUE ORDER BY nome";
        List<Mecanico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(ler(rs));
        }
        return lista;
    }

    private Mecanico ler(ResultSet rs) throws SQLException {
        Mecanico m = new Mecanico();
        m.setId(rs.getInt("id"));
        m.setNome(rs.getString("nome"));
        m.setEspecialidade(rs.getString("especialidade"));
        m.setTelefone(rs.getString("telefone"));
        m.setAtivo(rs.getBoolean("ativo"));
        return m;
    }

    public void inserir(Mecanico m) throws SQLException {
        String sql = "INSERT INTO mecanico (nome, especialidade, telefone, ativo) VALUES (?, ?, ?, ?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setString(2, m.getEspecialidade());
            ps.setString(3, m.getTelefone());
            ps.setBoolean(4, m.isAtivo());
            ps.executeUpdate();
        }
    }

    public void atualizar(Mecanico m) throws SQLException {
        String sql = "UPDATE mecanico SET nome = ?, especialidade = ?, telefone = ?, ativo = ? WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setString(2, m.getEspecialidade());
            ps.setString(3, m.getTelefone());
            ps.setBoolean(4, m.isAtivo());
            ps.setInt(5, m.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM mecanico WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
