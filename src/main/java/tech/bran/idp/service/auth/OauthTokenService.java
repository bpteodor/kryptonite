package tech.bran.idp.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.bran.idp.api.model.TokenRequest;
import tech.bran.idp.api.model.TokenResponse;
import tech.bran.idp.config.AppConfig;
import tech.bran.idp.service.repo.ConfigStore;
import tech.bran.idp.service.repo.dto.AuthSession;
import tech.bran.idp.service.repo.dto.ClientConfig;
import tech.bran.idp.util.BasicAuthentication;
import tech.bran.idp.util.Util;
import tech.bran.idp.util.validation.Check;

/**
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OauthTokenService {

    final ConfigStore configRepo;
    final SessionService sessionService;
    final AppConfig config;
    final PasswordEncoder encoder;

    /**
     * - only basic auth supported
     * - for client_secret_post must buy the enterprise version :)
     */
    public TokenResponse exchange(TokenRequest req) {

        // todo check all errors

        // check grant
        Check.that(req.getGrantType()).isEqualTo("authorization_code")
                .orSendAuthorizationError("unsupported_grant_type", null);

        // check client credentials
        final BasicAuthentication cred = Util.decodeAuthorization(req.getCredentials());
        Check.that(cred).isNotNull().as("no credentials").orSend(401, "Unauthorized");
        assert cred != null; // to remove nasty warning

        // check client_id
        if (StringUtils.hasLength(req.getClientId())) {
            Check.that(req.getClientId()).isEqualTo(cred.getUsername())
                    .as("clinet_id missamatch").orSend(401, "Unauthorized");
        } else {
            req.setClientId(cred.getUsername()); // client_id not mandatory if client is authenticated
        }

        // check client
        final ClientConfig clientConfig = configRepo.fetchClientConfig(req.getClientId());
        Check.that(clientConfig).isNotNull().orSendAuthorizationError("invalid_request", "Unknown client");
        assert null != clientConfig; // remove warning

        Check.that(cred.getPassword()).isValid(p -> encoder.matches(p, clientConfig.getClientSecret()))
                .as("Client credentials").orSend(401, "Unauthorized");

        final AuthSession session = sessionService.getSessionByCode(req.getCode());
        Check.that(session).isNotNull()
                .orSendAuthorizationError("invalid_request", "Missing, invalid or expired 'code'");

        Check.that(req.getRedirectUri()).isEqualTo(session.getRequest().getRedirectUri())
                .orSendAuthorizationError("invalid_request", "Invalid 'redirect_uri'");

        log.debug("request valid");
        final String token = sessionService.generateAccessToken(session);

        return new TokenResponse()
                .setAccessToken(token)
                //todo .setRefreshToken()
                .setExpiresIn(config.getOauth().getTokenExpiration().toSeconds())
                .setScope(session.getRequest().getScope()); // TODO consent page?
    }

}
