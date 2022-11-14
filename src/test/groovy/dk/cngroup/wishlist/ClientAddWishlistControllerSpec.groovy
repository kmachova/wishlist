package dk.cngroup.wishlist

import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.ResponseStatusException
import spock.lang.Shared

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClientAddWishlistControllerSpec extends BaseSpec {
    private static final FILE_PARAM = 'csv'
    private static final ADD_WISHLIST_TEMPLATE = "/clients/client-management/{username}/addWishlist"

    @Shared
    private final addWishlistPath = pathFromTemplate(ADD_WISHLIST_TEMPLATE, VADER_USERNAME)

    def 'should fail when no product code is valid: #name'() {
        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof AllWishesAreInvalidException
        exception.message == errorMessage400('Whishlist was not added since it contains invalid product codes only')

        where:
        name                          | productCodesCsv
        'file with one product'       | mockCsvFile(randomProductCode())
        'file with multiple products' | mockCsvFile(randomProductCode(), randomProductCode(), randomProductCode())
    }

    def 'should fail when client does not exist'() {
        given:
        def nonExistingUsername = randomUserName()
        def existingProductCode = FAKER.space().agency()
        productRepository.save(createProduct(existingProductCode))

        def path = pathFromTemplate(ADD_WISHLIST_TEMPLATE, nonExistingUsername)
        def productCodesCsv = mockCsvFile(existingProductCode)

        when:
        def results = mockMvc.perform(multipart(path)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof ClientUsernameNotFoundException
        exception.message == errorMessage404("Client with username '$nonExistingUsername' does not exist")
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

    def mockCsvFile(String... productCodes) {
        String content = productCodes.join('\n')
        new MockMultipartFile('csv', null, 'text/csv', content.getBytes())
    }
}
