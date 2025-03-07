package li.selman.ddd.wire.infrastructure;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpringdocConfiguration {

    private final String version;

    SpringdocConfiguration(@Value("${application.version:undefined}") String version) {
        this.version = version;
    }

    @Bean
    public OpenAPI litesampleOpenAPI() {
        return new OpenAPI().info(swaggerInfo()).externalDocs(swaggerExternalDoc());
    }

    private Info swaggerInfo() {
        return new Info()
                .title("Project API")
                .description("Project description API")
                .version(version)
                .license(new License().name("No license"));
    }

    private ExternalDocumentation swaggerExternalDoc() {
        return new ExternalDocumentation().description("Project Documentation");
    }

    @Bean
    public GroupedOpenApi myAppAllOpenAPI() {
        return GroupedOpenApi.builder().group("all").pathsToMatch("/api/**").build();
    }
}
