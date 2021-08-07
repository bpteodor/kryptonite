package tech.bran.idp.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-5.1">Successful Token Response</a>
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    // OPTIONAL
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    // RECOMMENDED
    @JsonProperty("expires_in")
    private long expiresIn;

    // OPTIONAL if identical to the scope requested by the client
    private String scope;
}
