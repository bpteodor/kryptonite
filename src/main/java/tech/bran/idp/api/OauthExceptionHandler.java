package tech.bran.idp.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.bran.idp.api.model.OauthErrorResponse;
import tech.bran.idp.util.Util;
import tech.bran.idp.util.validation.AuthzResponseException;
import tech.bran.idp.util.validation.ResponseException;

@Slf4j
@ControllerAdvice
public class OauthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResponseException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleValidationError(ResponseException ex) {
        log.info("showing error in page: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(new OauthErrorResponse().setError(ex.getMessage()));
    }

    @ExceptionHandler({AuthzResponseException.class})
    protected ResponseEntity<Object> handleAuthzResponse(AuthzResponseException ex) {

        if (null == ex.getRequest())
            return ResponseEntity.badRequest().body(new OauthErrorResponse().setError(ex.getMessage()));

        final String errUrl = Util.authzErr(ex.getRequest(), ex.getError(), ex.getDescription());
        log.info("redirecting to {}", errUrl);
        return ResponseEntity.status(302).header("location", errUrl).build();
    }
}
