package tech.bran.idp.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OauthErrorResponse {

    private String error; // REQUIRED

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error_uri")
    private String errorUri;
}
