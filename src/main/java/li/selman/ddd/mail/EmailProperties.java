package li.selman.ddd.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "ddd.email")
public record EmailProperties(String from) {

    public EmailProperties {
        Assert.hasText(from, "'from' must not be empty");
    }
}
