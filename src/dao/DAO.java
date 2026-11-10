package dao;

import java.util.List;

/**
 * Interface DAO (Data Access Object): o contrato que toda classe de acesso a uma tabela cumpre.
 * O <T> é o tipo da entidade, então o mesmo contrato serve para CarroDAO (DAO<Carro>),
 * ServicoDAO (DAO<Servico>), MecanicoDAO (DAO<Mecanico>) e AgendamentoDAO (DAO<Agendamento>).
 * Os controllers conhecem só esta interface: não sabem como cada DAO monta o seu SQL.
 */
public interface DAO<T> {

    /** Rótulos das colunas que a tela pode usar para ordenar a lista (ex.: "Nome", "Preço"). */
    String[] colunas();

    /** Lista os registros que contêm a busca, ordenados por até dois critérios de colunas(). */
    List<T> listar(String busca, String ordem1, String ordem2);

    /** Grava um novo registro (INSERT). */
    void inserir(T obj);

    /** Altera um registro existente, identificado pelo id do objeto (UPDATE). */
    void atualizar(T obj);

    /** Exclui o registro com esse id (DELETE), respeitando as chaves estrangeiras do banco. */
    void excluir(int id);
}
