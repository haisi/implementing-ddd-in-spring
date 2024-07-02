package li.selman.ddd.common;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
public abstract class AbstractWebIntegrationTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    LinkDiscoverers links;

    protected MockMvc mvc;

    @BeforeEach
    void setUp() {

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .defaultRequest(MockMvcRequestBuilders.get("/").locale(Locale.US))
                .build();
    }

    /**
     * Creates a {@link ResultMatcher} that checks for the presence of a link with the given rel.
     *
     * @param rel
     * @return
     */
    protected ResultMatcher linkWithRelIsPresent(LinkRelation rel) {
        return new LinkWithRelMatcher(rel, true);
    }

    /**
     * Creates a {@link ResultMatcher} that checks for the non-presence of a link with the given rel.
     *
     * @param rel
     * @return
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

            return String.format("Expected to %s link with relation %s, but found %s!",
                    present ? "find" : "not find", rel, present ? link.get() : "none");
        }
    }
}
