package tech.bran.idp.service.repo;

import org.springframework.stereotype.Repository;
import tech.bran.idp.service.repo.dto.AuthSession;

import java.util.HashMap;
import java.util.Map;

/**
 * db for session & tokens
 */
@Repository
public class TokenRepository {

    final Map<String, AuthSession> sessions = new HashMap<>();

    public AuthSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
}
