package tech.bran.idp.service.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import tech.bran.idp.service.repo.dto.ClientConfig;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * dummy config db
 */
@RequiredArgsConstructor
@Repository
public class ConfigRepository {

    final PasswordEncoder encoder;

    final Map<String, ClientConfig> clients = Map.of(
            "demo", new ClientConfig()
                    .setClientId("demo")
                    .setClientSecret("demo")
                    .setType(ClientConfig.ClientType.CONFIDENTIAL)
                    .setRedirectUris(Set.of("http://localhost/callback"))
                    .setAllowedScopes(Set.of("profile", "email", "phone"))
    );

    @PostConstruct
    public void init() {
        // simulate the password was saved hashed
        clients.values().forEach(i -> i.setClientSecret(encoder.encode(i.getClientSecret())));
    }

    public ClientConfig fetchClientConfig(String clientId) {
        return clients.get(clientId);
    }
}
