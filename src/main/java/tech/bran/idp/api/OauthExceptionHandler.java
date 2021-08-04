package tech.bran.idp.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.bran.idp.api.model.OauthErrorResponse;
import tech.bran.idp.service.validation.ErrorResponseException;

@Slf4j
@ControllerAdvice
public class OauthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ErrorResponseException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected OauthErrorResponse handleConflict(ErrorResponseException ex) {
        log.info("sending error: {}", ex.getMessage());
        return new OauthErrorResponse().setError(ex.getMessage());
    }
}
