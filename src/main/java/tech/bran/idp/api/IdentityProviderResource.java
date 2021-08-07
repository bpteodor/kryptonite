package tech.bran.idp.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tech.bran.idp.service.auth.LoginService;
import tech.bran.idp.util.Const;

/**
 * Identity Provider endpoints
 */
@RequiredArgsConstructor
@Controller
@RequestMapping
public class IdentityProviderResource {

    private final LoginService loginService;

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @CookieValue(name = Const.SSO_COOKIE_NAME) String ssoCookie) {
        return loginService.login(username, password, ssoCookie);
    }
}
