package tech.bran.idp.service.repo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ClientConfig {

    @NotBlank
    private String clientId;

    private String clientSecret;

    @NotNull
    private ClientType type;

    @NotEmpty
    private Set<String> redirectUris;

    private Set<String> allowedScopes;


    public enum ClientType {
        CONFIDENTIAL,
        PUBLIC
    }
}
