package dk.cngroup.wishlist

import static dk.cngroup.wishlist.TestUtils.extractResponseBody
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClientProductControllerSpec extends BaseSpec {

    def 'should return clients products'() {
        given:

        when:
        def response = mockMvc.perform(get('/clients_products'))

        then:
        response.andExpect(status().isOk())

//        and:
//        assertThatJson(extractResponseBody(response))
//                .isEqualTo('')
    }

}
