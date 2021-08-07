package tech.bran.idp.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.api.model.TokenRequest;
import tech.bran.idp.api.model.TokenResponse;
import tech.bran.idp.service.oauth.AuthzService;
import tech.bran.idp.service.oauth.TokenService;
import tech.bran.idp.util.Const;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Controller
@RequestMapping
public class OauthResource {

    private final AuthzService authzService;
    private final TokenService tokenService;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("response_type") String responseType,
                            @RequestParam("client_id") String clientId,
                            @RequestParam(value = "scope", required = false) String scope,
                            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                            @RequestParam(value = "state", required = false) String state,
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
    @ResponseBody
    public TokenResponse token(@RequestParam("grant_type") String grantType,
                               @RequestParam("code") String code,
                               @RequestParam("redirect_uri") String redirectUri,
                               @RequestParam("client_id") String clientId,
                               HttpServletRequest request) {
        return tokenService.exchange(new TokenRequest()
                .setGrantType(grantType)
                .setCode(code)
                .setRedirectUri(redirectUri)
                .setClientId(clientId)
                .setCredentials(request.getHeader("Authorization")));
    }
}
