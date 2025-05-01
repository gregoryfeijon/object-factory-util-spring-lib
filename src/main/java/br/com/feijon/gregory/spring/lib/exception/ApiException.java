package br.com.feijon.gregory.spring.lib.exception;

import java.io.Serial;
import java.util.List;

public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 697767481629667239L;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(List<String> messages) {
        super(buildErrorMessage(messages));
    }

    public ApiException(Throwable ex) {
        super(ex);
    }

    public ApiException(String message, Throwable ex) {
        super(message, ex);
    }

    private static String buildErrorMessage(List<String> errors) {
        StringBuilder sb = new StringBuilder();
        errors.forEach(error -> {
            if (error.contains(":")) {
                sb.append(error).append("\n\n");
            } else {
                sb.append(error).append("\n");
            }
        });
        return sb.toString();
    }
}
