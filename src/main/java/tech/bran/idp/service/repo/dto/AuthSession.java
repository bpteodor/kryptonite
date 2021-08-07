package tech.bran.idp.service.repo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.bran.idp.api.model.AuthzRequest;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AuthSession {

    private AuthzRequest request;

    private String subject;         // username

    private LocalDateTime expiration; // when the session expires

    private String authzCode;       // code returned by authorization endpoint

}
