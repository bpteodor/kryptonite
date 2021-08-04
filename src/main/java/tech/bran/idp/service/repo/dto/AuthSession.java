package tech.bran.idp.service.repo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AuthSession {
    private String subject;
    private LocalDateTime exp;
}
