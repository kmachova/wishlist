package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.exception.InvalidProductInBodyException
import net.javacrumbs.jsonunit.core.Option
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Shared

import static dk.cngroup.wishlist.TestUtils.expectedError
import static dk.cngroup.wishlist.TestUtils.extractException
import static dk.cngroup.wishlist.TestUtils.extractResponseBody
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static dk.cngroup.wishlist.TestUtils.randomColor
import static dk.cngroup.wishlist.TestUtils.complexColor
import static dk.cngroup.wishlist.TestUtils.OBJECT_MAPPER

class ProductControllerSpec extends BaseSpec {

    private static final PRODUCT_PATH = '/product'
    private static final APPLICATION_JSON = 'application/json'

    private static final INVALID_PRODUCT_MESSAGE = 'The product in the request body is invalid.'
    private static final INVALID_CODE_ERROR_MESSAGE = 'Product code can not be blank'

    @Shared
    private final productCode = randomProductCode()

    @Shared
    private final color = complexColor()

    def 'should save product with code only when code is #name'() {
        given:
        def requestBody = productDtoJson(productCodeRaw)

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType(APPLICATION_JSON)
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.code').value(productCode))
                .andExpect(jsonPath('$.color').doesNotExist())

        and:
        productExistsInDatabase(response)

        where:
        name                                      | productCodeRaw
        'exact'                                   | productCode
        'with leading whitespaces'                | "\\t\\t$productCode"
        'with trailing whitespace'                | "$productCode "
        'with multiple whitespaces in the middle' | productCode.replace(' ', ' \\t \\t ')
    }

    def 'should save product with code and color when color is #name'() {
        given:
        def productCode = randomProductCode()

        def requestBody = productDtoJson(productCode, colorRaw)

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType(APPLICATION_JSON))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code').value(productCode))
                .andExpect(jsonPath('$.color').value(color))

        and:
        productExistsInDatabase(response)

        where:
        name                                      | colorRaw
        'exact'                                   | color
        'with leading whitespaces'                | " $color"
        'with trailing whitespace'                | "$color \\t "
        'with multiple whitespaces in the middle' | color.replace(' ', '  ')
    }

    def 'should not save product with blank code (#productCode)'() {
        given:
        def expectedStatus = HttpStatus.BAD_REQUEST

        def requestBody = productDtoJson(productCode)

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType(APPLICATION_JSON)
        )

        then:
        response.andExpect(
                status().is(expectedStatus.value())
        )

        and:
        extractException(response) instanceof InvalidProductInBodyException
        assertThatJson(extractResponseBody(response))
                .isEqualTo(expectedError(expectedStatus, INVALID_PRODUCT_MESSAGE, [INVALID_CODE_ERROR_MESSAGE], true))

        where:
        productCode << [' ', '\\n', '\\t', '\\t\\t\\t', ' \\t ']
    }

    def 'should not save product with invalid color (#color)'() {
        given:
        def expectedStatus = HttpStatus.BAD_REQUEST

        and:
        def productCode = randomProductCode()
        def requestBody = productDtoJson(productCode, color)

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType(APPLICATION_JSON)
        )

        then:
        response.andExpect(
                status().is(expectedStatus.value())
        )

        and:
        extractException(response) instanceof InvalidProductInBodyException
        assertThatJson(extractResponseBody(response))
                .isEqualTo(expectedError(expectedStatus, INVALID_PRODUCT_MESSAGE, [INVALID_COLOR_MESSAGE], true))

        where:
        color <<
                ["${randomColor()}22", "${randomColor()}_${randomColor()}", "#${randomColor()}", "(${randomColor()})",
                 "${randomColor().toUpperCase()}", "${randomColor().capitalize()}"]
    }

    def 'should not save product with invalid code and color'() {
        given:
        def expectedStatus = HttpStatus.BAD_REQUEST

        def requestBody = productDtoJson(' ', '42')

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType(APPLICATION_JSON)
        )

        then:
        response.andExpect(
                status().is(expectedStatus.value())
        )

        and:
        extractException(response) instanceof InvalidProductInBodyException
        assertThatJson(extractResponseBody(response))
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedError(
                        expectedStatus,
                        INVALID_PRODUCT_MESSAGE,
                        [INVALID_CODE_ERROR_MESSAGE, INVALID_COLOR_MESSAGE],
                        true))
    }

    String productDtoJson(String code, String color = '') {
        def colorJson = color.isEmpty() ? color : """\n, "color":"$color\""""
        """
        {
            "code":"$code"$colorJson
        }
        """
    }

    Boolean productExistsInDatabase(ResultActions response) {
        def productJson = extractResponseBody(response)
        def productId = OBJECT_MAPPER.readValue(productJson, Product).id
        productRepository.existsById(productId)
    }

}
