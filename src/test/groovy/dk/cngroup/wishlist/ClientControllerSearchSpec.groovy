package dk.cngroup.wishlist

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.bind.MissingServletRequestParameterException
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.hamcrest.collection.IsCollectionWithSize.hasSize

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerSearchSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    private static final PRODUCT_CODE_PARAM = 'productCode'
    private static final CLIENTS_SEARCH_PATH = '/clients/search'

    def 'should return correct number of clients: #expectedClientCount'() {
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
        3                   | 'Death Star'
    }

    def 'clients should be sorted by their username'() {
        given: 'product in wishlists of 3 clients'
        def productCode = 'Death Star'

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].userName').value('DARTH_VADER'))
                .andExpect(jsonPath('$[1].userName').value('KYLO_REN'))
                .andExpect(jsonPath('$[2].userName').value('LUKE_SKYWALKER'))
    }

    def 'should return empty result when productCode param is #name'() {
        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$').isEmpty())

        where:
        name             | productCode
        'empty'          | ''
        'non-existing'   | 'non-exist code'
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

