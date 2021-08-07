package tech.bran.idp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import tech.bran.idp.service.repo.dto.ClientConfig;
import tech.bran.idp.service.repo.dto.UserData;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties
@Validated
public class AppConfig {

    @NotNull
    private OauthConfig oauth;

    @NotNull
    private IdpConfig idp;

    @NotEmpty
    private Map<String, ClientConfig> clients;

    @NotEmpty
    final List<UserData> users;

    @Data
    public static class OauthConfig {

        @NotBlank
        private String issuer;

        @NotNull
        private Duration tokenExpiration;

        @NotNull
        private String tokenSignatureHMAC;
    }

    @Data
    public static class IdpConfig {

        @NotNull
        private Duration ssoTimeout;

        /**
         * if the user doesn't proceed in this time he/she will have to start again
         */
        @NotNull
        private Duration authTimeout;
    }

}
