package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.ProductCodeNotFoundException
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.MissingServletRequestParameterException
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ClientSearchControllerSpec extends Specification implements DBTestData {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private ProductRepository productRepository

    private static final PRODUCT_CODE_PARAM = 'productCode'
    private static final CLIENTS_SEARCH_PATH = '/clients/search'
    private static final PRODUCT_CODE_IN_ALL_LISTS = 'Death Star'

    def 'should return correct number of clients: #expectedClientCount'() {
        given:
        fullSetup(clientRepository, productRepository)

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(expectedClientCount)))

        where:
        expectedClientCount | productCode
        0                   | 'sand'
        1                   | 'TIE Fighter'
        2                   | 'Star Destroyer'
        3                   | PRODUCT_CODE_IN_ALL_LISTS
    }

    def 'should return clients in correct format'() {
        given:
        oneClientWithWishesSetup(clientRepository)
        def expectedResponse = new File('src/test/resources/responses/DarthVaderWithWishlists.json').text

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, PRODUCT_CODE_IN_ALL_LISTS))

        then:
        def response = results
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        and:
        assertThatJson(response)
                .isEqualTo(expectedResponse)
    }

    def 'clients should be sorted by their username'() {
        given:
        fullSetup(clientRepository, productRepository)

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, PRODUCT_CODE_IN_ALL_LISTS))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].userName').value('DARTH_VADER'))
                .andExpect(jsonPath('$[1].userName').value('KYLO_REN'))
                .andExpect(jsonPath('$[2].userName').value('LUKE_SKYWALKER'))
    }

    def 'should return 404 when productCode param does not exist'() {
        given:
        def nonExistingProductCode = 'non-exist code 34856453'
        def expectedErrorMessage = "404 NOT_FOUND \"Product code '$nonExistingProductCode' specified in the query parameter does not exist\""

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, nonExistingProductCode))

        then:
        def exception = results
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof ProductCodeNotFoundException
        exception.getMessage() == expectedErrorMessage
    }

    def 'should fail when productCode param is #name'() {
        given:
        def expectedErrorMessage = "Required request parameter 'productCode' for method parameter type String is not present"

        when:
        def results = mockMvc.perform(get(path))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof MissingServletRequestParameterException
        exception.getMessage() == expectedErrorMessage

        where:
        name      | path
        'missing' | CLIENTS_SEARCH_PATH
        'flag'    | "$CLIENTS_SEARCH_PATH?$PRODUCT_CODE_PARAM"
    }
}

