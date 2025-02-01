package li.selman.ddd.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.net.URI;

@ConfigurationProperties(prefix = "ddd.s3")
public record S3Properties(URI url, String accessKeyId, String secretAccessKey) {
    public S3Properties {
        Assert.notNull(url, "url must not be null");
        Assert.hasText(accessKeyId, "accessKeyId must not be empty");
        Assert.hasText(secretAccessKey, "secretAccessKey must not be empty");
    }
}
