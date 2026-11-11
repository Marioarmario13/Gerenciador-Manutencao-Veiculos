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

/** DAO da tabela servico. */
public class ServicoDAO implements DAO<Servico> {

    // opções de "Ordenar por": nome na tela -> coluna no SQL
    private static final Map<String, String> COLUNAS = new LinkedHashMap<>();
    static {
        COLUNAS.put("Nome", "nome");
        COLUNAS.put("Preço", "preco");
        COLUNAS.put("Intervalo (km)", "intervalo_km");
        COLUNAS.put("Código", "id");
    }

    @Override
    public String[] colunas() {
        return COLUNAS.keySet().toArray(new String[0]);
    }

    @Override
    public List<Servico> listar(String busca, String ordem1, String ordem2) {
        String sql = "SELECT id, nome, descricao, preco, intervalo_km FROM servico "
                + "WHERE nome LIKE ? OR descricao LIKE ? OR CAST(preco AS CHAR) LIKE ? "
                + "OR REPLACE(CAST(preco AS CHAR), '.', ',') LIKE ? "
                + "OR CAST(intervalo_km AS CHAR) LIKE ? OR CAST(id AS CHAR) LIKE ?"
                + Conexao.ordenarPor(COLUNAS, ordem1, ordem2);
        List<Servico> lista = new ArrayList<>();
        // try com recursos: a conexão e o comando são fechados sozinhos no final, mesmo se der erro
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            // % é o coringa do LIKE: "%troca%" encontra qualquer texto que tenha "troca".
            // Com a busca vazia fica "%%", que traz tudo. O mesmo filtro vai em cada ?.
            String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
            for (int i = 1; i <= 6; i++) ps.setString(i, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                // cada linha do resultado (ResultSet) vira um objeto Servico
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
            // o erro do MySQL vira uma mensagem em português (ver AppException)
            throw new AppException(e);
        }
        return lista;
    }

    @Override
    public void inserir(Servico s) {
        Conexao.executar("INSERT INTO servico (nome, descricao, preco, intervalo_km) VALUES (?, ?, ?, ?)",
                s.getNome(), s.getDescricao(), s.getPreco(), s.getIntervaloKm());
    }

    @Override
    public void atualizar(Servico s) {
        Conexao.executar("UPDATE servico SET nome = ?, descricao = ?, preco = ?, intervalo_km = ? WHERE id = ?",
                s.getNome(), s.getDescricao(), s.getPreco(), s.getIntervaloKm(), s.getId());
    }

    @Override
    public void excluir(int id) {
        Conexao.executar("DELETE FROM servico WHERE id = ?", id);
    }
}
