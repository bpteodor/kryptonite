package tech.bran.idp.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.bran.idp.api.model.OauthErrorResponse;
import tech.bran.idp.util.validation.AuthzResponseException;
import tech.bran.idp.util.validation.ErrorResponseException;
import tech.bran.idp.util.Util;

@Slf4j
@ControllerAdvice
public class OauthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ErrorResponseException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected OauthErrorResponse handleValidationError(ErrorResponseException ex) {
        log.info("showing error in page: {}", ex.getMessage());
        return new OauthErrorResponse().setError(ex.getMessage());
    }

    @ExceptionHandler({AuthzResponseException.class})
    protected String handleAuthzResponse(AuthzResponseException ex) {
        final String errUrl = Util.authzErr(ex.getRequest(), ex.getError(), ex.getDescription());
        log.info("redirecting oauth style error: {}", errUrl);
        return "redirect:" + errUrl;
    }
}
