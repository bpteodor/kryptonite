package tech.bran.idp.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.config.AppConfig;
import tech.bran.idp.service.repo.ConfigStore;
import tech.bran.idp.service.repo.TokenRepository;
import tech.bran.idp.service.repo.dto.AuthSession;
import tech.bran.idp.service.repo.dto.ClientConfig;
import tech.bran.idp.util.validation.Check;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * handles the authorization endpoint logic
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OauthAuthorizeService {

    final ConfigStore configRepo;
    final TokenRepository tokenRepo;
    final AppConfig config;
    final SessionService sessionService;

    public String auth(AuthzRequest req, String ssoCookie) {

        // check client
        Check.that(req.getClientId()).matches("[\\w_-]+").orSend("unauthorized_client");
        final ClientConfig client = configRepo.fetchClientConfig(req.getClientId());
        Check.that(client).isNotNull().orSend("unauthorized_client");

        // check redirect_uri
        Check.that(req.getRedirectUri()).emptyOrValidUri().orSend("Invalid 'redirect_uri'");
        if (!isEmpty(client.getRedirectUris())) {
            Check.that(req.getRedirectUri()).isContainedIn(client.getRedirectUris()).orSend("Invalid 'redirect_uri'");
        }

        // check response_type
        Check.that(req.getResponseType()).isEqualTo("code")
                .orSendAuthorizationError(req, "unsupported_response_type", "Only the Authorization code flow is supported");

        // check scopes
        final String[] scopes = StringUtils.hasLength(req.getScope()) ? req.getScope().split("\\s+") : new String[]{};
        Check.that(scopes).areContainedIn(client.getAllowedScopes()).orSendAuthorizationError(req, "invalid_scope");

        log.trace("/authorize validation passed");

        final AuthSession sso = sessionService.getValidSession(ssoCookie);
        if (null != sso) {
            log.info("session is valid");
            sso.setRequest(req);
            return sessionService.authzSuccess(sso);
        }

        log.info("no valid session found, go to login...");
        sessionService.generateAuthSession(req);

        return "redirect:/login.html";
    }

}
