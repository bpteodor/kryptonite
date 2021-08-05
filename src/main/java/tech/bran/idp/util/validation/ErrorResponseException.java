package tech.bran.idp.util.validation;

public class ErrorResponseException extends RuntimeException {
    public ErrorResponseException(String msg) {
        super(msg);
    }
}
