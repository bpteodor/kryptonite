package tech.bran.idp.tests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.service.auth.SessionService;
import tech.bran.idp.service.repo.TokenRepository;
import tech.bran.idp.service.repo.dto.AuthSession;
import tech.bran.idp.util.Const;

import javax.servlet.http.Cookie;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
@ActiveProfiles({"test"})
public class TestAuthorizationEndpoint {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    SessionService sessionService;

    @Autowired
    TokenRepository tokenRepository;

    final String redirectUri = "http://localhost/callback";

    @Test
    void testRedirectToLoginPage() throws Exception {
        final String ssoCookie = mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "email profile")
                        .queryParam("state", UUID.randomUUID().toString())
                        .queryParam("redirect_uri", redirectUri))
                .andExpect(status().isFound())
                .andExpect(header().string("location", is("/login.html")))
                .andExpect(cookie().value(Const.SSO_COOKIE_NAME, notNullValue()))
                .andReturn().getResponse().getCookie(Const.SSO_COOKIE_NAME).getValue();

        Assertions.assertThat(sessionService.getValidSession(ssoCookie)).isNotNull();
    }

    @Test
    void testReturnCode() throws Exception {

        // set dummy SSO cookie in db
        final String ssoId = "dummy-session-2";
        tokenRepository.saveSession(ssoId, new AuthSession()
                .setExpiration(LocalDateTime.now().plus(Duration.ofMinutes(42)))
                .setRequest(new AuthzRequest()
                        .setClientId("demo")
                        .setScope("email profile")
                        .setRedirectUri(redirectUri)));

        final String state = UUID.randomUUID().toString();
        mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "email profile")
                        .queryParam("state", state)
                        .queryParam("redirect_uri", redirectUri)
                        .cookie(new Cookie(Const.SSO_COOKIE_NAME, ssoId)))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", allOf(
                        startsWith(redirectUri),
                        containsString("code"),
                        not(containsString("error")))));
    }

    @Test
    void testValidateResponseType() throws Exception {
        mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "invalid")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "email profile")
                        .queryParam("state", UUID.randomUUID().toString())
                        .queryParam("redirect_uri", redirectUri))
                .andExpect(status().isFound())
                .andExpect(cookie().doesNotExist(Const.SSO_COOKIE_NAME))
                .andExpect(redirectedUrlPattern(redirectUri + "?error=unsupported_response_type*"));
    }

    @Test
    void testValidateUnknownClient() throws Exception {
        mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "unknown")
                        .queryParam("scope", "email profile")
                        .queryParam("state", UUID.randomUUID().toString())
                        .queryParam("redirect_uri", redirectUri))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(Const.SSO_COOKIE_NAME))
                .andExpect(jsonPath("$.error").value("unauthorized_client"));
    }

    @Test
    void testValidateScope() throws Exception {
        mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "open_doors")
                        .queryParam("state", UUID.randomUUID().toString())
                        .queryParam("redirect_uri", redirectUri))
                .andExpect(status().isFound())
                .andExpect(cookie().doesNotExist(Const.SSO_COOKIE_NAME))
                .andExpect(redirectedUrlPattern(redirectUri + "?error=invalid_scope*"));
    }

    @Test
    void testValidateRedirectUri() throws Exception {
        mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "email profile")
                        .queryParam("state", UUID.randomUUID().toString())
                        .queryParam("redirect_uri", "https://bad-hacker.com"))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(Const.SSO_COOKIE_NAME))
                .andExpect(jsonPath("$.error").value("invalid_request"));
    }
}
