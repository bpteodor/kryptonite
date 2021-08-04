package tech.bran.idp.service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "idp")
public class AppConfig {

    //@Value("${sso.secret}")
    //private String ssoJwtHmacKey;

    @Value("${sso.timeout:1h}")
    private Duration ssoTimeout;


    @Value("${oauth.token.timeout:20m}")
    private Duration tokenTimeout;
}
