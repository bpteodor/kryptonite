package tech.bran.idp.service.validation;

public class ErrorResponseException extends RuntimeException {
    public ErrorResponseException(String msg) {
        super(msg);
    }
}
