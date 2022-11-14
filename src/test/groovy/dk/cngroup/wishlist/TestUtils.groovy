package dk.cngroup.wishlist

import org.springframework.web.util.UriTemplate

trait TestUtils {
    String pathFromTemplate(String stringUriTemplate, String... uriVariables) {
        def uriTemplate = new UriTemplate(stringUriTemplate)
        uriTemplate.expand(uriVariables)
    }

    String errorMessage400(String innerMessage) { "400 BAD_REQUEST \"$innerMessage\"" }

    String errorMessage404(String innerMessage) { "404 NOT_FOUND \"$innerMessage\"" }
}