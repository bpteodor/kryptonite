package tech.bran.idp.service.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import tech.bran.idp.service.repo.dto.UserData;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * dummy user data
 */
@RequiredArgsConstructor
@Repository
public class UserRepository {

    final PasswordEncoder encoder;

    List<UserData> users = List.of(
            new UserData().setUsername("teo@example.com").setPassword("secret"),
            new UserData().setUsername("max@example.com").setPassword("secret"),
            new UserData().setUsername("ana@example.com").setPassword("secret")
    );

    @PostConstruct
    public void init() {
        // simulate the password was saved hashed
        users.forEach(u -> u.setPassword(encoder.encode(u.getPassword())));
    }

    public UserData search(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findAny().orElse(null);
    }
}
