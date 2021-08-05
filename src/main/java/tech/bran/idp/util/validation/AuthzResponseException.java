package tech.bran.idp.util.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.bran.idp.api.model.AuthzRequest;

/**
 * this error should be handled like an authorization response:
 * https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1
 */
@Getter
@RequiredArgsConstructor
public class AuthzResponseException extends RuntimeException {

    private final AuthzRequest request;
    private final String error;
    private final String description;
}
