package dk.cngroup.wishlist

import dk.cngroup.wishlist.exception.ProductCodeNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingServletRequestParameterException

import static dk.cngroup.wishlist.TestUtils.expectedError
import static dk.cngroup.wishlist.TestUtils.extractException
import static dk.cngroup.wishlist.TestUtils.extractResponseBody
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.thenIgnore
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import static net.javacrumbs.jsonunit.core.Option.IGNORING_EXTRA_FIELDS
import static net.javacrumbs.jsonunit.core.Option.IGNORING_VALUES
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.path
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.paths
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.then
import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import static org.hamcrest.text.MatchesPattern.matchesPattern
import static dk.cngroup.wishlist.TestUtils.responseJsonToString

class ClientSearchControllerSpec extends BaseSpec {

    private static final PRODUCT_CODE_PARAM = 'productCode'
    private static final CLIENTS_SEARCH_PATH = '/clients/search'

    def 'should return correct number of clients: #expectedClientCount'() {
        given:
        fullSetup()

        when:
        def response = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(expectedClientCount)))

        where:
        productCode         || expectedClientCount
        'sand'              || 0
        TIE_FIGHTER_CODE    || 1
        STAR_DESTROYER_CODE || 2
        DEATH_STAR_CODE     || 3
    }

    def 'should return clients in correct format'() {
        given:
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)

        when:
        def response = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, DEATH_STAR_CODE))

        then:
        response
                .andExpect(status().isOk())

        and:
        def responseBody = response
                .andReturn()
                .getResponse()
                .getContentAsString()

        assertThatJson(responseBody)
                .isArray()
                .hasSize(1)

        assertThatJson(responseBody)
                .withMatcher('timeStampRegex', matchesPattern('[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[0-9.]*'))
                .inPath('$.[0]')
        //.node('[0]')
                .isObject()
                .isEqualTo(VADER_JSON)

        and: 'alternative with JsonUnit options'
        def vaderJsonWithMissingFields = responseJsonToString('DarthVaderWithWishesWithMissingFields')
        def pathToProducts = '[0].wishes[0].products[*]'

        assertThatJson(responseBody)
                .when(path('[0].id'), thenIgnore())
                .when(paths('[0].wishes[0].id', "${pathToProducts}.id"), then(IGNORING_VALUES))
                .when(path(pathToProducts), then(IGNORING_EXTRA_FIELDS))
                .node('[0]')
                .isEqualTo(vaderJsonWithMissingFields)
    }

    def 'single client should be returned only once'() {
        given: 'client with the same product in multiple wishlists'
        vader.addWishlist(wishlist1Product)
        vader.addWishlist(wishlist2Products)
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)

        when:
        def response = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, DEATH_STAR_CODE))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].lastName').value('Vader'))
    }

    def 'clients should be sorted by their username'() {
        given:
        fullSetup()

        when:
        def response = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, DEATH_STAR_CODE))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].userName').value(VADER_USERNAME))
                .andExpect(jsonPath('$[1].userName').value('KYLO_REN'))
                .andExpect(jsonPath('$[2].userName').value('LUKE_SKYWALKER'))
    }

    def 'should return 404 when productCode param does not exist'() {
        given:
        def nonExistingProductCode = randomProductCode()

        and:
        def expectedStatus = HttpStatus.NOT_FOUND
        def expectedErrorMessage = "Product code '$nonExistingProductCode' specified in the query parameter does not exist"

        when:
        def response = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, nonExistingProductCode))

        then:
        response.andExpect(
                status().is(expectedStatus.value())
        )

        and:
        extractException(response) instanceof ProductCodeNotFoundException
        assertThatJson(extractResponseBody(response))
                .isEqualTo(expectedError(expectedStatus, expectedErrorMessage))
    }

    def 'should not consider case of the productCode param ("#productCode")'() {
        given:
        vader.addWishlist(wishlist1Product)
        clientRepository.save(vader)

        when:
        def response = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(json()
                        .when(IGNORING_EXTRA_FIELDS)
                        .isEqualTo("[{firstName: 'Darth', lastName: 'Vader', wishes: '#{json-unit.ignore}'}]"))

        where:
        productCode << [DEATH_STAR_CODE,
                        DEATH_STAR_CODE.toUpperCase(),
                        DEATH_STAR_CODE.toLowerCase(),
                        DEATH_STAR_CODE.replace('h', 'H')
        ]
    }

    def 'should fail when productCode param is #name'() {
        given:
        def expectedStatus = HttpStatus.BAD_REQUEST
        def expectedErrorMessage = "Required request parameter 'productCode' for method parameter type String is not present"

        when:
        def response = mockMvc.perform(get(path))

        then:
        response.andExpect(
                status().is(expectedStatus.value())
        )

        and:
        extractException(response) instanceof MissingServletRequestParameterException
        assertThatJson(extractResponseBody(response))
                .isEqualTo(expectedError(expectedStatus, expectedErrorMessage))

        where:
        name      | path
        'missing' | CLIENTS_SEARCH_PATH
        'flag'    | "$CLIENTS_SEARCH_PATH?$PRODUCT_CODE_PARAM"
    }

}
