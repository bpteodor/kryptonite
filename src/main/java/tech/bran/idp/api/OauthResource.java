package tech.bran.idp.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.api.model.TokenRequest;
import tech.bran.idp.service.oauth.AuthzService;
import tech.bran.idp.util.Const;

@RequiredArgsConstructor
@Controller
@RequestMapping
public class OauthResource {

    private final AuthzService authzService;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("response_type") String responseType,
                            @RequestParam("client_id") String clientId,
                            @RequestParam(value = "scope", required = false) String scope,
                            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                            @RequestParam(value = "state", required = false) String state,
                            //@RequestParam("code_challenge_method") String codeChallengeMethod, TODO pixie
                            //@RequestParam("code_challenge") String codeChallenge
                            @CookieValue(name = Const.SSO_COOKIE_NAME, required = false) String ssoCookie) {
        return authzService.auth(
                new AuthzRequest()
                        .setResponseType(responseType)
                        .setClientId(clientId)
                        .setScope(scope)
                        .setRedirectUri(redirectUri)
                        .setState(state),
                ssoCookie);
    }


    @PostMapping("/token")
    public void token(@RequestBody TokenRequest req) {
        // todo
    }
}
