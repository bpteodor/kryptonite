package tech.bran.idp.service.repo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class ClientConfig {

    private String clientId;
    private String clientSecret;
    private ClientType type;
    private Set<String> redirectUris;
    private Set<String> allowedScopes;


    public enum ClientType {
        CONFIDENTIAL,
        PUBLIC
    }
}
