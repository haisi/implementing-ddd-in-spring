package li.selman.ddd.common;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class AbstractWebIntegrationTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    LinkDiscoverers links;

    protected MockMvc mvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .defaultRequest(get("/").locale(Locale.US))
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .build();
    }

    /**
     * Creates a {@link ResultMatcher} that checks for the presence of a link with the given rel.
     */
    protected ResultMatcher linkWithRelIsPresent(LinkRelation rel) {
        return new LinkWithRelMatcher(rel, true);
    }

    /**
     * Creates a {@link ResultMatcher} that checks for the non-presence of a link with the given rel.
     */
    protected ResultMatcher linkWithRelIsNotPresent(LinkRelation rel) {
        return new LinkWithRelMatcher(rel, false);
    }

    protected LinkDiscoverer getDiscovererFor(MockHttpServletResponse response) {
        return links.getRequiredLinkDiscovererFor(Objects.requireNonNull(response.getContentType()));
    }

    private class LinkWithRelMatcher implements ResultMatcher {

        private final LinkRelation rel;
        private final boolean present;

        private LinkWithRelMatcher(LinkRelation rel, boolean present) {
            this.rel = rel;
            this.present = present;
        }

        @Override
        public void match(MvcResult result) throws Exception {

            MockHttpServletResponse response = result.getResponse();
            String content = response.getContentAsString();
            LinkDiscoverer discoverer = links.getRequiredLinkDiscovererFor(response.getContentType());

            Optional<Link> link = discoverer.findLinkWithRel(rel, content);

            assertThat(link).matches(it -> it.isPresent() == present, getMessage(link));
        }

        private String getMessage(Optional<Link> link) {

            return String.format(
                    "Expected to %s link with relation %s, but found %s!",
                    present ? "find" : "not find", rel, present ? link.get() : "none");
        }
    }
}
