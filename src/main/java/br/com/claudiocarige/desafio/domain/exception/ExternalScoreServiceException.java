package br.com.claudiocarige.desafio.domain.exception;

public class ExternalScoreServiceException extends RuntimeException {

    private Integer statusCode;
    public ExternalScoreServiceException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ExternalScoreServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
