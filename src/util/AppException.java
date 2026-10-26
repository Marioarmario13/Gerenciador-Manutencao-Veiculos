package util;

import java.sql.SQLException;

/** Erro com mensagem pronta para mostrar ao usuário (validação ou erro do banco). */
public class AppException extends RuntimeException {

    public AppException(String mensagem) {
        super(mensagem);
    }

    /** Converte um erro do MySQL em uma mensagem fácil de entender. */
    public AppException(SQLException e) {
        super(traduzir(e));
    }

    private static String traduzir(SQLException e) {
        String estado = e.getSQLState();
        int codigo = e.getErrorCode();
        if (codigo == 1451) {
            return "Não é possível excluir: existem registros vinculados a este item "
                    + "(ex.: serviço na agenda de um carro ou mecânico atribuído a um carro).";
        }
        if (codigo == 1452) {
            return "O registro relacionado não existe mais. Atualize a lista e tente novamente.";
        }
        if (codigo == 1062) {
            return "Já existe um registro igual (valor duplicado), como uma placa já cadastrada "
                    + "ou o mesmo serviço na mesma data para o carro.";
        }
        if (codigo == 1406) {
            return "Um dos campos passou do tamanho máximo permitido. Use um texto mais curto.";
        }
        if (codigo == 1044 || codigo == 1045 || "28000".equals(estado)) {
            return "Acesso negado ao MySQL. Confira usuário e senha no config.properties.";
        }
        if (codigo == 1049) {
            return "Banco 'manutencao_veiculos' não encontrado. Execute sql/schema.sql no MySQL.";
        }
        if (estado != null && estado.startsWith("08")) {
            return "Não foi possível conectar ao MySQL. Verifique se o servidor está ligado, "
                    + "o config.properties e se o driver JDBC está na pasta lib/.";
        }
        return "Erro no banco de dados: " + e.getMessage();
    }
}
