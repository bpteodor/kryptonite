package tech.bran.idp.config;

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

    /**
     * if the user doesn't finish to auth in this time he/she will have to start again
     */
    @Value("${sso.timeout:5m}")
    private Duration authTimeout;


    @Value("${oauth.token.timeout:20m}")
    private Duration tokenTimeout;
}
