package tech.bran.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan("tech.bran.idp.config")
@EnableConfigurationProperties
public class KryptoniteApplication {

    public static void main(String[] args) {
        SpringApplication.run(KryptoniteApplication.class, args);
    }

}
