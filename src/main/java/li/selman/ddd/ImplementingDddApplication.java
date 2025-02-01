package li.selman.ddd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith
@ConfigurationPropertiesScan
public class ImplementingDddApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImplementingDddApplication.class, args);
    }

}
