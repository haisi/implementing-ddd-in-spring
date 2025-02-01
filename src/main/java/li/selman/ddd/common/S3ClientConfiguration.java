package li.selman.ddd.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Configuration
class S3ClientConfiguration {

    @Bean
    S3Client getAmazonS3Client(S3Properties s3Properties) {
        return S3Client.builder()
                .endpointProvider(new S3EndpointProvider() {
                    @Override
                    public CompletableFuture<Endpoint> resolveEndpoint(S3EndpointParams s3EndpointParams) {
                        return CompletableFuture.completedFuture(Endpoint.builder()
                                .url(s3Properties.url().resolve("ddd-test"))
                                .build());
                    }

                    @Override
                    public CompletableFuture<Endpoint> resolveEndpoint(
                            Consumer<S3EndpointParams.Builder> endpointParamsConsumer) {
                        return S3EndpointProvider.super.resolveEndpoint(endpointParamsConsumer);
                    }
                })
                .credentialsProvider(
                        () -> AwsBasicCredentials.create(s3Properties.accessKeyId(), s3Properties.secretAccessKey()))
                .forcePathStyle(true)
                .build();
    }
}
