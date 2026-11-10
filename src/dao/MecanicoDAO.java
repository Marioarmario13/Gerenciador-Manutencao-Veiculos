package dao;

import model.Mecanico;
import util.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Acesso à tabela mecanico: implementa o contrato DAO<Mecanico>. */
public class MecanicoDAO implements DAO<Mecanico> {

    /** Critérios de ordenação disponíveis (rótulo na tela -> coluna SQL). */
    private static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Nome", "nome");
        COLUNAS.put("Especialidade", "especialidade");
        COLUNAS.put("Em atividade", "ativo");
        COLUNAS.put("Código", "id");
    }

    @Override
    public String[] colunas() {
        return COLUNAS.keySet().toArray(new String[0]);
    }

    @Override
    public List<Mecanico> listar(String busca, String ordem1, String ordem2) {
        String sql = "SELECT id, nome, especialidade, telefone, ativo FROM mecanico "
                + "WHERE nome LIKE ? OR especialidade LIKE ? OR telefone LIKE ? "
                + "OR CAST(id AS CHAR) LIKE ? "
                + "OR CASE WHEN ativo = TRUE THEN 'Sim' ELSE 'Não' END LIKE ? "
                + Conexao.ordenarPor(COLUNAS, ordem1, ordem2);
        List<Mecanico> lista = new ArrayList<>();
        try (Connection conexao = Conexao.getConnection(); PreparedStatement ps = conexao.prepareStatement(sql)) {
            String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
            for (int i = 1; i <= 5; i++) ps.setString(i, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mecanico mecanico = new Mecanico();
                    mecanico.setId(rs.getInt("id"));
                    mecanico.setNome(rs.getString("nome"));
                    mecanico.setEspecialidade(rs.getString("especialidade"));
                    mecanico.setTelefone(rs.getString("telefone"));
                    mecanico.setAtivo(rs.getBoolean("ativo"));
                    lista.add(mecanico);
                }
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
        return lista;
    }

    @Override
    public void inserir(Mecanico m) {
        Conexao.executar("INSERT INTO mecanico (nome, especialidade, telefone, ativo) VALUES (?, ?, ?, ?)",
                m.getNome(), m.getEspecialidade(), m.getTelefone(), m.isAtivo());
    }

    @Override
    public void atualizar(Mecanico m) {
        Conexao.executar("UPDATE mecanico SET nome = ?, especialidade = ?, telefone = ?, ativo = ? WHERE id = ?",
                m.getNome(), m.getEspecialidade(), m.getTelefone(), m.isAtivo(), m.getId());
    }

    @Override
    public void excluir(int id) {
        Conexao.executar("DELETE FROM mecanico WHERE id = ?", id);
    }
}
