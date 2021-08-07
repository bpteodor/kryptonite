package tech.bran.idp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "idp")
public class AppConfig {

    // OAuth ----------------------------------

    @Value("${oauth.issuer}")
    private String issuer;

    @Value("${oauth.token.expiration:20m}")
    private Duration tokenExpiration;

    @Value("${oauth.token.signature.HMAC}")
    private String tokenSignatureHMAC;


    // IdP -------------------------------------

    @Value("${sso.timeout:1h}")
    private Duration ssoTimeout;

    /**
     * if the user doesn't proceed in this time he/she will have to start again
     */
    @Value("${sso.timeout:10m}")
    private Duration authTimeout;


    // General ---------------------------------

}
