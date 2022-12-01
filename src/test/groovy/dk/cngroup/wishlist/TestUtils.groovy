package dk.cngroup.wishlist

import com.github.javafaker.Faker
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.util.UriTemplate
import groovy.text.SimpleTemplateEngine

class TestUtils {

    static final FAKER = new Faker()
    private static final TEMPLATE_ENGINE = new SimpleTemplateEngine()
    private static final String TEST_RESOURCE_PATH = 'src/test/resources'
    private static final String ERROR_TEMPLATE = fileToText("$TEST_RESOURCE_PATH/json_templates/errorResponse.json")

    static String pathFromTemplate(String stringUriTemplate, String... uriVariables) {
        def uriTemplate = new UriTemplate(stringUriTemplate)
        uriTemplate.expand(uriVariables)
    }

    static String fileToText(String path) {
        new File(path).text
    }

    static String responseJsonToString(String fileName, String pathInResources = 'responses') {
        fileToText("$TEST_RESOURCE_PATH/$pathInResources/${fileName}.json")
    }

    static String expectedError(HttpStatus status, String message = '#{json-unit.any-string}', List<String> params = []) {
        def paramsString = params.size() == 0 ?
                '' : ",\nparameters:[${params.collect { param -> '\n{' + param + '}' }.join(',')}]"

        def bindMap = [
                status    : status.reasonPhrase,
                statusCode: status.value(),
                message   : message,
                parameters: paramsString
        ]
        getTemplated(ERROR_TEMPLATE, bindMap)
    }

    static String getTemplated(String template, Map bindMap) {
        TEMPLATE_ENGINE.createTemplate(template).make(bindMap).toString()
    }

    static List<String> getTemplatedList(String template, List<Map> maps) {
        maps.collect { map -> getTemplated(template, map) }
    }

    static String extractResponseBody(ResultActions response) {
        response
                .andReturn()
                .getResponse()
                .getContentAsString()
    }

    static Exception extractException(ResultActions response) {
        response
                .andReturn()
                .getResolvedException()
    }

    static String randomWord() {
        FAKER.lorem().word()
    }

}
