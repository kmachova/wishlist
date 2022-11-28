package dk.cngroup.wishlist

import com.github.javafaker.Faker
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class FetchClientsTest {

    @Autowired
    lateinit var clientRepository: ClientRepository

    companion object {
        private val faker = Faker()

        private val productCode = faker.space().moon()

        private val firstName: String = faker.address().firstName()
        private val lastName: String = faker.address().lastName()
        val userName = "${firstName}_${lastName}".uppercase()

        val client = Client(firstName = firstName, lastName = lastName)
    }

    @BeforeEach
    fun saveClient() {
        clientRepository.save(client)
    }

    //check log to see the difference in SQL executed by Hibernate
    @Test
    fun `test default behavior`() {
        clientRepository.getByUserName(userName)
    }

    @Test
    fun `test fetch orders`() {
        clientRepository.findByUserName(userName)
    }

    @Test
    fun `test fetch orders and products`() {
        clientRepository.findClientByUserName(userName)
    }

    @Test
    fun `test search client id by product code`() {
        clientRepository.findDistinctByWishesProductsCodeIgnoreCaseOrderByUserName(productCode)
    }
}
