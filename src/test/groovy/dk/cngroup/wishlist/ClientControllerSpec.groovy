package dk.cngroup.wishlist

import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClientControllerSpec extends BaseSpec {

    def 'Expected JSON response is created for a valid request'() {
        given:
        clientRepository.save(vader)
        clearEntityManager()

        when:
        def response = mockMvc.perform(get("/clients/client-management/$VADER_USERNAME"))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.lastName', equalTo('Vader')))
    }

    def '404 is returned for invalid userName'() {
        when:
        def response = mockMvc.perform(get('/clients/client-management/FOO'))

        then:
        response.andExpect(status().isNotFound())
    }

}
