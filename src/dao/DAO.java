package dao;

import java.util.List;

/**
 * Interface do padrão DAO: o que todo DAO de cadastro precisa ter.
 * O T é o tipo da entidade, então a mesma interface serve para Carro, Servico,
 * Mecanico e Agendamento. Os controllers usam esta interface e não precisam saber
 * como cada DAO monta o SQL.
 */
public interface DAO<T> {

    // colunas que aparecem em "Ordenar por"
    String[] colunas();

    // lista o que contém o texto da busca, ordenado por até dois critérios
    List<T> listar(String busca, String ordem1, String ordem2);

    // INSERT
    void inserir(T obj);

    // UPDATE (usa o id do objeto)
    void atualizar(T obj);

    // DELETE pelo id
    void excluir(int id);
}
