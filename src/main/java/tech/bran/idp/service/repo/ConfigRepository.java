package tech.bran.idp.service.repo;

import org.springframework.stereotype.Repository;
import tech.bran.idp.service.repo.dto.ClientConfig;

import java.util.Map;
import java.util.Set;

/**
 * dummy config db
 */
@Repository
public class ConfigRepository {

    final Map<String, ClientConfig> clients = Map.of(
            "demo", new ClientConfig()
                    .setClientId("demo")
                    .setClientSecret("demo")
                    .setRedirectUris(Set.of("http://localhost/callback"))
                    .setAllowedScopes(Set.of("profile", "email", "phone"))
    );

    public ClientConfig fetchClientConfig(String clientId) {
        return clients.get(clientId);
    }
}
