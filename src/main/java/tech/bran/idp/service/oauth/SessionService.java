package tech.bran.idp.service.oauth;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.config.AppConfig;
import tech.bran.idp.service.repo.TokenRepository;
import tech.bran.idp.service.repo.dto.AuthSession;
import tech.bran.idp.util.Const;
import tech.bran.idp.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static tech.bran.idp.util.Util.exp;
import static tech.bran.idp.util.Util.now;

/**
 * Handles session, tokens, authorization codes
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService {

    final AppConfig config;
    final TokenRepository tokenRepo;
    final HttpServletResponse response;

    public AuthSession getValidSession(String ssoId) {
        final AuthSession sso = tokenRepo.getSession(ssoId);
        if (sso == null || sso.getExpiration() == null) {
            log.info("session {} found", ssoId);
            return null;
        }
        if (now().isBefore(sso.getExpiration().plus(config.getIdp().getSsoTimeout()))) {
            log.info("session {} expired", ssoId);
            return null;
        }
        return sso;
    }

    /**
     * generates, persists and sets a cookie / sso session
     */
    public AuthSession generateAuthSession(AuthzRequest req) {

        final String ssoId = UUID.randomUUID().toString();
        final AuthSession sso = new AuthSession()
                .setRequest(req)
                .setExpiration(exp(config.getIdp().getAuthTimeout()));

        tokenRepo.saveSession(ssoId, sso);

        Util.setCookie(response, Const.SSO_COOKIE_NAME, ssoId, config.getIdp().getAuthTimeout());

        log.debug("generated new session {}", ssoId);
        return sso;
    }

    /**
     * Authorization Response - success
     */
    public String authzSuccess(AuthSession sso) {
        final String redirectUri = sso.getRequest().getRedirectUri();
        Assert.hasLength(redirectUri, "expected redirect_uri");

        final String authzCode = UUID.randomUUID().toString();

        // update session expiration time
        sso.setAuthzCode(authzCode)
                .setExpiration(Util.exp(config.getIdp().getAuthTimeout()));

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("code", authzCode)
                .queryParamIfPresent("state", Optional.ofNullable(sso.getRequest().getState()))
                .build().toUriString();
    }

    public AuthSession getSessionByCode(String code) {
        return tokenRepo.getSessionByCode(code);
    }

    /**
     * @param session valid session
     * @return new access_token
     * @see <a href="https://github.com/FusionAuth/fusionauth-jwt">repo</a>
     */
    public String generateAccessToken(AuthSession session) {

        // in the enterprise version use algorithm configured for client :)
        final Signer signer = HMACSigner.newSHA256Signer(config.getOauth().getTokenSignatureHMAC());

        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        // Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)
        final JWT jwt = new JWT().setIssuer(config.getOauth().getIssuer())
                .setIssuedAt(now)
                .setSubject(session.getSubject())
                .setExpiration(now.plus(config.getOauth().getTokenExpiration()));

        if (StringUtils.hasLength(session.getRequest().getScope()))
            jwt.addClaim("scope", session.getRequest().getScope());

        // Sign and encode the JWT to a JSON string representation
        log.info("access token generated");
        return JWT.getEncoder().encode(jwt, signer);
    }
}
