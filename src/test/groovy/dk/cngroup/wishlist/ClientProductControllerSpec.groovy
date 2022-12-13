package dk.cngroup.wishlist

import dk.cngroup.wishlist.exception.InvalidSorByException
import org.springframework.http.HttpStatus

import static dk.cngroup.wishlist.TestUtils.FAKER
import static dk.cngroup.wishlist.TestUtils.expectedError
import static dk.cngroup.wishlist.TestUtils.extractException
import static dk.cngroup.wishlist.TestUtils.extractResponseBody
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import static dk.cngroup.wishlist.helper.RandomDataGenerator.saveRandomClientsWithWishlists

class ClientProductControllerSpec extends BaseSpec {

    private static final TESTED_PATH = '/clients_products'
    private static final PAGE_PARAM = 'page'
    private static final SIZE_PARAM = 'size'
    private static final SORT_PARAM = 'sort'

    private static final DEFAULT_SIZE = 5

    def setup() {
        clientRepository.deleteAll()
    }

    def 'should return default page, size and sorting when no query param specified'() {
        given:
        def nProducts = [new kotlin.ranges.IntRange(1, 1)]
        saveRandomClientsWithWishlists(clientRepository, 50, nProducts)
        clearEntityManager()

        when:
        def response = mockMvc.perform(get(TESTED_PATH))

        then:
        response.andExpect(status().isOk())

        and:
        assertThatJson(extractResponseBody(response))
                .node('page').isEqualTo("""{"size":$DEFAULT_SIZE,"totalElements":50,"totalPages":10,"number":0}""".toString())
    }

    def 'should return page info when database is empty'() {
        when:
        def response = mockMvc.perform(get(TESTED_PATH))

        then:
        response.andExpect(status().isOk())

        and:
        assertThatJson(extractResponseBody(response))
                .isEqualTo("""{"_links":{"self":{"href":"${pageLink()}"}},""".toString() +
                        """"page":{"size":$DEFAULT_SIZE,"totalElements":0,"totalPages":0,"number":0}}""")
    }

    def 'should not return the same client product multiple times'() {
        given: 'client with 6 products which are 3 distinct products'
        vader.addWishlist(wishlist3Products)
        vader.addWishlist(wishlist2Products)
        vader.addWishlist(wishlist1Product)
        clientRepository.save(vader)
        clearEntityManager()

        when:
        def response = mockMvc.perform(get(TESTED_PATH))

        then:
        response.andExpect(status().isOk())

        and:
        assertThatJson(extractResponseBody(response))
                .node('_embedded.clientProductDtoes').isArray().hasSize(3)
    }

    def 'should return default when invalid values of #param query param'() {
        given:
        def nProducts = [new kotlin.ranges.IntRange(1, 1)]
        saveRandomClientsWithWishlists(clientRepository, 50, nProducts)
        clearEntityManager()

        when:
        def response = mockMvc.perform(get(TESTED_PATH)
                .param(param, value.toString()))

        then:
        response.andExpect(status().isOk())

        and:
        assertThatJson(extractResponseBody(response)).with {
            node('page.size').isEqualTo(5)
            node('page.number').isEqualTo(0)
        }

        where:
        name            | param      | value
        'negative page' | PAGE_PARAM | -1
        'negative size' | SIZE_PARAM | -1
        'string page'   | PAGE_PARAM | FAKER.lorem().word()
        'string size'   | SIZE_PARAM | FAKER.lorem().word()
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

    def 'should return maximum size when page is too big (#size)'() {
        given:
        def nProducts = [new kotlin.ranges.IntRange(1, 1)]
        def nClients = 50
        saveRandomClientsWithWishlists(clientRepository, nClients, nProducts)
        clearEntityManager()

        def maxPageSize = 1000

        when:
        def response = mockMvc.perform(get(TESTED_PATH)
                .param(SIZE_PARAM, size.toString()))

        then:
        response.andExpect(status().isOk())

        and:
        assertThatJson(extractResponseBody(response)).with {
            node('page.size').isEqualTo(maxPageSize)
            node('page.number').isEqualTo(0)
            node('_embedded.clientProductDtoes').isArray().hasSize(nClients)
        }

        where:
        size << [1001, 2000]
    }

    static String pageLink(int page = 0, int size = DEFAULT_SIZE, String sort = 'id,asc') {
        "http://localhost/clients_products?page=$page&size=$size&sort=$sort"
    }

}
