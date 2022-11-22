package dk.cngroup.wishlist

import org.springframework.web.util.UriTemplate

trait TestUtils {
    static String pathFromTemplate(String stringUriTemplate, String... uriVariables) {
        def uriTemplate = new UriTemplate(stringUriTemplate)
        uriTemplate.expand(uriVariables)
    }

    static String errorMessage400(String innerMessage) { "400 BAD_REQUEST \"$innerMessage\"" }

    static String errorMessage404(String innerMessage) { "404 NOT_FOUND \"$innerMessage\"" }

    static String responseJsonToString(String fileName, String pathInResources = 'responses') {
        new File("src/test/resources/$pathInResources/${fileName}.json").text
    }
}