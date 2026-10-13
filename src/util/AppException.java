package util;

/** Erro de negócio/validação com mensagem pronta para mostrar ao usuário. */
public class AppException extends RuntimeException {
    public AppException(String mensagem) {
        super(mensagem);
    }
}
