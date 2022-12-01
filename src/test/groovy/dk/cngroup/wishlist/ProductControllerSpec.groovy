package dk.cngroup.wishlist

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static dk.cngroup.wishlist.TestUtils.FAKER

class ProductControllerSpec extends BaseSpec {

    private static final PRODUCT_PATH = '/product'

    def 'should save product'() {
        given:
        def productCode = randomProductCode()
        def requestBody = productDtoJson(productCode)

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType('application/json')
        )

        then:
        response.andExpect(status().isOk())
    }

    def 'should not save product'() {
        given:
        def productCode = randomProductCode()
        def requestBody = productDtoJson(productCode, "${FAKER.color().name()}22")

        when:
        def response = mockMvc.perform(post(PRODUCT_PATH)
                .content(requestBody)
                .contentType('application/json')
        )

        then:
        response.andExpect(status().isBadRequest())
    }

    String productDtoJson(String code, String color = FAKER.color().name()) {
        """
        {
                "code":"$code",
                "color":"$color"
        }
        """
    }

}
