package dk.cngroup.wishlist

import dk.cngroup.wishlist.exception.InvalidSorByException
import org.springframework.http.HttpStatus

import static dk.cngroup.wishlist.TestUtils.expectedError
import static dk.cngroup.wishlist.TestUtils.extractException
import static dk.cngroup.wishlist.TestUtils.extractResponseBody
import static dk.cngroup.wishlist.TestUtils.RANDOM
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson

class ClientProductControllerSpec extends BaseSpec {

    private static final TESTED_PATH = '/clients_products'
    private static final PAGE_PARAM = 'page'
    private static final SIZE_PARAM = 'size'
    private static final SORT_PARAM = 'sort'

    def setup() {
        clientRepository.deleteAll()
    }

    def 'should return clients products with links'() {
        when:
        def response = mockMvc.perform(get(TESTED_PATH))

        then:
        response.andExpect(status().isOk())

//        and:
//        assertThatJson(extractResponseBody(response))
//                .isEqualTo('')
    }

    def 'should ignore invalid values of #param query param'() {
        when:
        def response = mockMvc.perform(get(TESTED_PATH)
                .param(param, value.toString()))

        then:
        response.andExpect(status().isOk())

        where:
        name            | param      | value
        'negative page' | PAGE_PARAM | -1
        'negative size' | SIZE_PARAM | -(RANDOM.nextInt(10) + 1)
        'too big page'  | PAGE_PARAM | 200
        'too big size'  | SIZE_PARAM | 2001
    }

    def 'should fail when sort contains invalid properties'() {
        given:
        def expectedStatus = HttpStatus.BAD_REQUEST
        def expectedMessage = "Result can not be sort by following properties: [$messagePart]. " +
                "Allowed are ['clientId', 'firstName', 'id', 'lastName', 'productCode', 'productColor', 'productId']."

        when:
        def response = mockMvc.perform(get(TESTED_PATH)
                .param(SORT_PARAM, sortValue.toString()))

        then:
        response.andExpect(
                status().is(expectedStatus.value())
        )

        and:
        extractException(response) instanceof InvalidSorByException
        assertThatJson(extractResponseBody(response))
                .isEqualTo(expectedError(expectedStatus, expectedMessage))

        where:
        sortValue                       | messagePart
        'dummyString'                   | "'dummyString'"
        'clientId,invalidOne,productId' | "'invalidOne'"
        'ClientId,ID'                   | "'ClientId', 'ID'"
        'Hi,how,are,you'                | "'Hi', 'how', 'are', 'you'"
    }

}
