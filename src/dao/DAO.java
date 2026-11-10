package dao;

import java.util.List;

/**
 * Interface DAO (Data Access Object): o contrato de CRUD das tabelas de cadastro.
 * O <T> é o tipo da entidade, então o mesmo contrato serve para CarroDAO (DAO<Carro>),
 * ServicoDAO (DAO<Servico>) e MecanicoDAO (DAO<Mecanico>). Os controllers usam esta
 * interface, sem depender de como cada DAO monta o seu SQL.
 */
public interface DAO<T> {

    /** Lista os registros que contêm a busca, ordenados por até dois critérios (colunas da tela). */
    List<T> listar(String busca, String ordem1, String ordem2);

    /** Grava um novo registro (INSERT). */
    void inserir(T obj);

    /** Altera um registro existente, identificado pelo id do objeto (UPDATE). */
    void atualizar(T obj);

    /** Exclui o registro com esse id (DELETE), respeitando as chaves estrangeiras do banco. */
    void excluir(int id);
}
