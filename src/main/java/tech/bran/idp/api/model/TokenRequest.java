package tech.bran.idp.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TokenRequest {

    String grantType;
    String code;
    String redirectUri;
    String clientId;

    String credentials;
}
