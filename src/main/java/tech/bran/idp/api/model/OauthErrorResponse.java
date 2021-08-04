package tech.bran.idp.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OauthErrorResponse {
    private String error; // REQUIRED
    private String error_description;
    private String error_uri;
}
