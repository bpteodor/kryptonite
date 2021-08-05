package tech.bran.idp.util.validation;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseException extends RuntimeException {

    private int httpStatus = 400;

    public ResponseException(String msg) {
        super(msg);
    }
}
