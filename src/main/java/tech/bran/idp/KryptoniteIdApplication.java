package tech.bran.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("tech.bran.idp.service.config")
public class KryptoniteIdApplication {

    public static void main(String[] args) {
        SpringApplication.run(KryptoniteIdApplication.class, args);
    }

}
