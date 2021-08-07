package tech.bran.idp.service.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import tech.bran.idp.config.AppConfig;
import tech.bran.idp.service.repo.dto.UserData;

import javax.annotation.PostConstruct;

/**
 * user db mock
 */
@Slf4j
@RequiredArgsConstructor
@Repository
@ConfigurationProperties
public class UserStore {

    final PasswordEncoder encoder;
    final AppConfig config;

    @PostConstruct
    public void init() {
        log.info("{} user loaded", config.getUsers().size());
        // simulate the password was saved hashed
        config.getUsers().forEach(u -> u.setPassword(encoder.encode(u.getPassword())));
    }

    public UserData search(String username) {
        return config.getUsers().stream().filter(u -> u.getUsername().equals(username)).findAny().orElse(null);
    }
}
