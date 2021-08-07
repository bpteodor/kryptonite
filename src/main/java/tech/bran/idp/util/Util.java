package tech.bran.idp.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import tech.bran.idp.api.model.AuthzRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

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


    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * @return expiration time
     */
    public static LocalDateTime exp(Duration timeout) {
        return LocalDateTime.now().plus(timeout);
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

    public static BasicAuthentication decodeAuthorization(String authorization) {

        if (!StringUtils.hasLength(authorization) || !authorization.matches("Basic .*")) return null;

        try {
            final String base64Credentials = authorization.substring("Basic ".length());
            final byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            final String credentials = new String(credDecoded, UTF_8);
            final String[] values = credentials.split(":", 2);
            if (values.length == 2) return new BasicAuthentication(values[0], values[1]);
        } catch (RuntimeException e) {
            // not important
        }
        return null;
    }
}
