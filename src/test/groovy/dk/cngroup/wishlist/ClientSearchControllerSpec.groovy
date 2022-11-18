package dk.cngroup.wishlist

import org.springframework.web.bind.MissingServletRequestParameterException

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson

class ClientSearchControllerSpec extends BaseSpec {
    private static final PRODUCT_CODE_PARAM = 'productCode'
    private static final CLIENTS_SEARCH_PATH = '/clients/search'

    def 'should return correct number of clients: #expectedClientCount'() {
        given:
        fullSetup()

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(expectedClientCount)))

        where:
        productCode    || expectedClientCount
        'sand'         || 0
        TIE_FIGHTER    || 1
        STAR_DESTROYER || 2
        DEATH_STAR     || 3
    }

    def 'should return clients in correct format'() {
        given:
        //productRepository.saveAll([deathStar, starDestroyer, tieFighter])
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)
        //clientRepository.findByUserName(VADER_USERNAME)
        clearEntityManager()

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, DEATH_STAR))

        then:
        def response = results
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        and:
        assertThatJson(response).isArray().hasSize(1)
        assertThatJson(response).node('[0]').isEqualTo(VADER_JSON)
    }

    def 'single client should be returned only once'() {
        given: 'client with the same product in multiple wishlists'
        vader.addWishlist(wishlist1Product)
        vader.addWishlist(wishlist2Products)
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, DEATH_STAR))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].lastName').value('Vader'))
    }

    def 'clients should be sorted by their username'() {
        given:
        fullSetup()

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, DEATH_STAR))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].userName').value(VADER_USERNAME))
                .andExpect(jsonPath('$[1].userName').value('KYLO_REN'))
                .andExpect(jsonPath('$[2].userName').value('LUKE_SKYWALKER'))
    }

    def 'should return 404 when productCode param does not exist'() {
        given:
        def nonExistingProductCode = randomProductCode()
        def expectedErrorMessage = errorMessage404("Product code '$nonExistingProductCode' specified in the query parameter does not exist")

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
        exception.message == expectedErrorMessage
    }

    def 'should not consider case of the productCode param ("#productCode")'() {
        given:
        vader.addWishlist(wishlist1Product)
        clientRepository.save(vader)

        when:
        def results = mockMvc.perform(get(CLIENTS_SEARCH_PATH)
                .queryParam(PRODUCT_CODE_PARAM, productCode))

        then:
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].lastName').value('Vader'))

        where:
        productCode << [DEATH_STAR,
                        DEATH_STAR.toUpperCase(),
                        DEATH_STAR.toLowerCase(),
                        DEATH_STAR.replace('h', 'H')
        ]
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
        exception.message == expectedErrorMessage

        where:
        name      | path
        'missing' | CLIENTS_SEARCH_PATH
        'flag'    | "$CLIENTS_SEARCH_PATH?$PRODUCT_CODE_PARAM"
    }
}

