package tech.bran.idp.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.bran.idp.config.AppConfig;
import tech.bran.idp.service.repo.TokenRepository;
import tech.bran.idp.service.repo.UserStore;
import tech.bran.idp.service.repo.dto.AuthSession;
import tech.bran.idp.service.repo.dto.UserData;
import tech.bran.idp.util.validation.AuthzResponseException;

/**
 * handles login operation
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {

    final UserStore userRepo;
    final TokenRepository tokenRepo;
    final PasswordEncoder passwordEncoder;
    final SessionService sessionService;
    final AppConfig config;

    // todo: counter failed attempts, account lock, etc
    public String login(String username, String password, String ssoCookie) {

        final AuthSession session = tokenRepo.getSession(ssoCookie);
        if (session == null) {
            log.info("sso cookie {} not found. game over", ssoCookie);
            throw new AuthzResponseException(null, "invalid_request", "missing sso cookie");
        }

        final UserData user = userRepo.search(username);
        if (user == null) {
            log.info("unknown user {}. game over", username);
            //throw new AuthzResponseException(session.getRequest(), "access_denied", null);
            return "redirect:/login.html"; // back to login page
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.info("invalid credentials for user {}", username);
            //throw new AuthzResponseException(session.getRequest(), "access_denied", null);
            return "redirect:/login.html"; // back to login page
        }

        log.info("user {} logged in", username);
        return sessionService.authzSuccess(session);
    }

}
