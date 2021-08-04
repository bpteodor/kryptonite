package tech.bran.idp.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.service.config.AppConfig;
import tech.bran.idp.service.repo.ConfigRepository;
import tech.bran.idp.service.repo.TokenRepository;
import tech.bran.idp.service.repo.dto.AuthSession;
import tech.bran.idp.service.repo.dto.ClientConfig;
import tech.bran.idp.service.validation.Check;

import static org.springframework.util.CollectionUtils.isEmpty;
import static tech.bran.idp.util.Util.now;

/**
 * logic for the authorization endpoint
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthzService {

    final ConfigRepository configRepo;
    final TokenRepository tokenRepo;
    final AppConfig appConfig;

    public String auth(AuthzRequest req, String sso) {

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

        if (isSessionValid(sso)) {
            return authzCode(req, sso);
        }

        return "redirect:login";
    }

    private String authzCode(AuthzRequest req, String sso) {
        return "todo"; // TODO
    }

    /**
     * check
     */
    private boolean isSessionValid(String ssoId) {
        final AuthSession sso = tokenRepo.getSession(ssoId);
        log.debug("session {} found", ssoId);
        return sso != null && sso.getExp() != null && now().isBefore(sso.getExp().plus(appConfig.getSsoTimeout()));
    }

}
