package tech.bran.idp.util;

import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;
import tech.bran.idp.api.model.AuthzRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class Util {


    /**
     * Authorization Response - error
     */
    public static String authzErr(AuthzRequest req, String error, String description) {
        Assert.hasLength(req.getRedirectUri(), "expected redirect_uri");

        return UriComponentsBuilder.fromUriString(req.getRedirectUri())
                .queryParam("error", error)
                .queryParamIfPresent("error_description", Optional.ofNullable(description))
                .queryParamIfPresent("state", Optional.ofNullable(req.getState()))
                .build().toUriString();
    }


    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * @return expiration time
     */
    public static LocalDateTime exp(Duration timeout) {
        return now().plus(timeout);
    }

    public static Cookie setCookie(HttpServletResponse response, String name, String value, Duration timeout) {

        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge((int) timeout.toSeconds());
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); not for an exercise
        //cookie.setDomain(); todo cookie domain + path
        //cookie.setPath();

        response.addCookie(cookie);
        return cookie;
    }
}
