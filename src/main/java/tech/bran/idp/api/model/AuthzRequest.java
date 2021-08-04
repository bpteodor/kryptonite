package tech.bran.idp.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthzRequest {

    private String responseType;
    private String clientId;
    private String scope;
    private String redirectUri;
    private String state;

    // TODO pkce
    private String codeChallengeMethod;
    private String codeChallenge;
}
