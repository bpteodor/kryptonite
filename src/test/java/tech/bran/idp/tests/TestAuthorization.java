package tech.bran.idp.tests;

import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
@ActiveProfiles({"test"})
public class TestAuthorization {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    SessionService sessionService;

    @Autowired
    TokenRepository tokenRepository;

    @Test
    void testRedirectToLoginPage() throws Exception {
        final String ssoCookie = mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "email profile")
                        .queryParam("state", UUID.randomUUID().toString())
                        .queryParam("redirect_uri", "http://localhost/callback"))
                .andExpect(status().isFound())
                .andExpect(header().string("location", CoreMatchers.containsString("/login.html")))
                .andExpect(cookie().value(Const.SSO_COOKIE_NAME, CoreMatchers.notNullValue()))
                .andReturn().getResponse().getCookie(Const.SSO_COOKIE_NAME).getValue();

        Assertions.assertThat(sessionService.getValidSession(ssoCookie)).isNotNull();
    }

    @Test
    void testReturnCode() throws Exception {

        // set dummy SSO cookie in db
        final String ssoId = "dummy-session-2";
        tokenRepository.saveSession(ssoId, new AuthSession().setRequest(new AuthzRequest()
                .setClientId("demo")
                .setScope("email profile")
                .setRedirectUri("http://localhost/callback")));

        final String state = UUID.randomUUID().toString();
        mockMvc.perform(get("/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "demo")
                        .queryParam("scope", "email profile")
                        .queryParam("state", state)
                        .queryParam("redirect_uri", "http://localhost/callback")
                        .cookie(new Cookie(Const.SSO_COOKIE_NAME, ssoId)))
                .andExpect(status().isFound())
                .andExpect(header().string("location", CoreMatchers.containsString("/login")));
    }

}
