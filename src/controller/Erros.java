package controller;

import java.sql.SQLException;

/** Traduz erros do PostgreSQL/JDBC para mensagens amigáveis. */
class Erros {
    static String msg(SQLException e) {
        String estado = e.getSQLState();
        if ("23503".equals(estado)) {
            return "Não é possível excluir: existem registros vinculados a este item "
                    + "(ex.: serviço na agenda de um carro ou mecânico atribuído a um carro).";
        }
        if ("23505".equals(estado)) {
            return "Já existe um registro igual (valor duplicado), como uma placa já cadastrada "
                    + "ou o mesmo serviço na mesma data para o carro.";
        }
        if ("28P01".equals(estado)) {
            return "Acesso negado ao PostgreSQL. Confira usuário e senha no config.properties.";
        }
        if ("3D000".equals(estado)) {
            return "Banco 'manutencao_veiculos' não encontrado. Crie-o no pgAdmin e execute sql/schema.sql.";
        }
        if (estado != null && estado.startsWith("08")) {
            return "Não foi possível conectar ao PostgreSQL. Verifique se o servidor está ligado, "
                    + "o config.properties e se o driver JDBC está na pasta lib/.";
        }
        return "Erro no banco de dados: " + e.getMessage();
    }
}
