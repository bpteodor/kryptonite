package tech.bran.idp.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.service.repo.TokenRepository;
import tech.bran.idp.service.repo.dto.AuthSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
@ActiveProfiles({"test"})
public class TestTokenEndpoint {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    TokenRepository tokenRepository;

    final String redirectUri = "http://localhost/callback";

    @Test
    void happyPath() throws Exception {

        final String code = "dummy-code";

        // set dummy SSO cookie in db
        final String ssoId = "dummy-session-2";
        tokenRepository.saveSession(ssoId, new AuthSession()
                .setExpiration(LocalDateTime.now().plus(Duration.ofMinutes(42)))
                .setAuthzCode(code)
                .setRequest(new AuthzRequest()
                        .setClientId("demo")
                        .setScope("email profile")
                        .setRedirectUri(redirectUri)));

        mockMvc.perform(post("/token")
                        .contentType("application/x-www-form-urlencoded")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("demo:demo".getBytes()))
                        .content(UriComponentsBuilder.newInstance()
                                .queryParam("grant_type", "authorization_code")
                                .queryParam("code", code)
                                .queryParam("client_id", "demo")
                                .queryParam("redirect_uri", redirectUri)
                                .toUriString().substring(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.scope").value("email profile"))
                .andExpect(jsonPath("$.expires_in").value(900));
    }

    @Test
    void testInvalidCode() throws Exception {
        mockMvc.perform(post("/token")
                        .contentType("application/x-www-form-urlencoded")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("demo:demo".getBytes()))
                        .content(UriComponentsBuilder.newInstance()
                                .queryParam("grant_type", "authorization_code")
                                .queryParam("code", "invalid-code")
                                .queryParam("client_id", "demo")
                                .queryParam("redirect_uri", redirectUri)
                                .toUriString().substring(1)))
                .andExpect(status().isBadRequest());
    }

    // ...
}
