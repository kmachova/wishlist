package dk.cngroup.wishlist

import com.github.javafaker.Faker
import dk.cngroup.wishlist.entity.*
import lombok.RequiredArgsConstructor
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.*

@Component
@RequiredArgsConstructor
class DatabaseInitializer(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val faker = Faker()

        repeat(5) {
            val product = Product(code = faker.space().star())
            productRepository.save(product)
        }

        repeat(10) {
            val nProducts = (0..10).random()
            val products = (1..nProducts).map { Product(code = faker.space().planet()) }.toMutableList()

            val client = Client(firstName = faker.address().firstName(), lastName = faker.address().lastName())
            client.addWishlist(Wishlist(products = products))
            clientRepository.save(client)
        }
    }
}