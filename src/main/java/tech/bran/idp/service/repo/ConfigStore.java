package tech.bran.idp.service.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import tech.bran.idp.config.AppConfig;
import tech.bran.idp.service.repo.dto.ClientConfig;

import javax.annotation.PostConstruct;

/**
 * dummy config db
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class ConfigStore {

    final PasswordEncoder encoder;

    final AppConfig config;


    @PostConstruct
    public void init() {
        log.info("{} client configurations loaded", config.getClients().size());
        // simulate the password was saved hashed
        config.getClients().values().forEach(i -> i.setClientSecret(encoder.encode(i.getClientSecret())));
    }

    public ClientConfig fetchClientConfig(String clientId) {
        return config.getClients().get(clientId);
    }
}
