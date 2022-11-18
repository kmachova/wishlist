package dk.cngroup.wishlist

import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.ResponseStatusException
import spock.lang.Shared

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClientAddWishlistControllerSpec extends BaseSpec {
    private static final FILE_PARAM = 'csv'
    private static final ADD_WISHLIST_TEMPLATE = "/clients/client-management/{username}/addWishlist"

    @Shared
    private final String[] existingProductCodes = (0..<3).collect { FAKER.space().star() }

    @Shared
    private final String[] nonExistingProductCodes = (0..<2).collect { randomProductCode() }

    @Shared
    private final addWishlistPath = pathFromTemplate(ADD_WISHLIST_TEMPLATE, VADER_USERNAME)

    private final minimalValidFile = mockCsvFile(existingProductCodes[0])

    def setup() {
        productRepository.saveAll(existingProductCodes.collect { createProduct(it) })
    }

    def 'should add wishlist with codes only to client'() {
        given:
        clientRepository.save(vader)
        productRepository.saveAll([deathStar, starDestroyer, tieFighter])
        clearEntityManager()

        def productCodes = [deathStar.code, starDestroyer.code, tieFighter.code]

        def file = mockCsvFile(productCodes.join('\n'))

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(file))

        then:
        def response = results
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        and: 'wishlist was saved and linked to correct client'
        clientRepository.findByUserName(VADER_USERNAME)
                .wishes[0].products.collect { it.code } == productCodes

        and: 'response contains client with new wishlist in correct format'
        assertThatJson(response)
                .isEqualTo(VADER_JSON)
    }

    def 'should not override existing wishlists'() {
        given: 'client with 2 wishlists'
        vader.addWishlist(wishlist2Products)
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(minimalValidFile))

        then: 'number of wishlists was increased by 1'
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.wishes', hasSize(3)))
    }

    def 'should fail when file with wishes is empty'() {
        given:
        def productCodesCsv = mockCsvFile("")

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof ResponseStatusException
        exception.message == errorMessage400('File with wishes is empty')
    }

    def 'should return all errors when file with wishes is invalid'() {
        given:
        def expectedMessage = "Some of csv lines are invalid: [Line 2: Field 'code' is mandatory but no value was provided., " +
                "Line 4: Field 'code' is mandatory but no value was provided.]"

        def productCodesCsv = mockCsvFile(
                "${existingProductCodes[0]}\n,,\n${existingProductCodes[1]}\n  , \n${nonExistingProductCodes[0]}\n${existingProductCodes[2]}")

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof InvalidCsvLinesException
        exception.message == errorMessage400(expectedMessage)
    }

    def 'should fail when some of product codes are not found in product repo'() {
        given:
        def expectedMessage = "Wishlist was not created since some of products specified in the file do not exist. " +
                "Codes of these products are: ['${nonExistingProductCodes[0]}', '${nonExistingProductCodes[1]}']"

        def productCodesCsv = mockCsvFile([
                existingProductCodes[0],
                existingProductCodes[1],
                nonExistingProductCodes[0],
                existingProductCodes[2],
                nonExistingProductCodes[1]
        ].join('\n'))

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof InvalidProductCodesInFileException
        exception.message == errorMessage400(expectedMessage)
    }

    def 'should fail when client does not exist'() {
        given:
        def nonExistingUsername = randomUserName()

        def path = pathFromTemplate(ADD_WISHLIST_TEMPLATE, nonExistingUsername)

        when:
        def results = mockMvc.perform(multipart(path)
                .file(minimalValidFile))

        then:
        def exception = results
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof ClientUsernameNotFoundException
        exception.message == errorMessage404("Client with username '$nonExistingUsername' does not exist")
    }

    def 'should fail when productCode param is #name'() {
        when:
        def results = mockMvc.perform(multipart(path))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof MissingServletRequestPartException
        exception.message == "Required request part 'csv' is not present"

        where:
        name      | path
        'missing' | addWishlistPath
        'flag'    | "$addWishlistPath?$FILE_PARAM"
    }

    def mockCsvFile(String content) {
        new MockMultipartFile('csv', null, 'text/csv', content.getBytes())
    }
}
