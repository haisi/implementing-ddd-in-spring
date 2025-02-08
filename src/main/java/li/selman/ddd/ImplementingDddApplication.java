package li.selman.ddd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.Modulith;

@Modulith
@ConfigurationPropertiesScan
public class ImplementingDddApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImplementingDddApplication.class, args);
    }

}
