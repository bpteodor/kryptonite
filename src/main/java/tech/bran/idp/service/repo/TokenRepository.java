package tech.bran.idp.service.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tech.bran.idp.service.repo.dto.AuthSession;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.hasLength;

/**
 * db mock for session & tokens
 */
@Slf4j
@Repository
public class TokenRepository {

    final Map<String, AuthSession> sessions = new HashMap<>();

    public AuthSession getSession(String sessionId) {
        return hasLength(sessionId)
                ? sessions.get(sessionId)
                : null;
    }

    public AuthSession getSessionByCode(String code) {
        return hasLength(code)
                ? sessions.values().stream().filter(s -> code.equals(s.getAuthzCode())).findFirst().orElse(null)
                : null;
    }

    public AuthSession saveSession(String ssoId, AuthSession sso) {
        log.trace("saving session {}", ssoId);
        return sessions.put(ssoId, sso);
    }

}
