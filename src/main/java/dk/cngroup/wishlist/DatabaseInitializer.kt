package dk.cngroup.wishlist

import com.github.javafaker.Faker
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import lombok.RequiredArgsConstructor
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class DatabaseInitializer(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository
) : CommandLineRunner {

    val faker = Faker()

    override fun run(vararg args: String) {
        val nProductsInNoWishList = 5
        val nClients = 10

        repeat(nProductsInNoWishList) {
            val product = Product(code = faker.space().moon(), color = faker.color().name())
            productRepository.save(product)
        }

        repeat(nClients) {
            val nProducts = (0..10).random()
            val products = (1..nProducts).map { Product(code = faker.space().star(), color = faker.color().name()) }
                .toMutableList()

            val client = randomClient()
            client.addWishlist(Wishlist(products = products))
            client.addWishlist(Wishlist(products = mutableListOf(randomProduct())))
            clientRepository.save(client)
        }

        repeat(nClients) {
            clientRepository.save(randomClient())
        }
    }

    private fun randomClient() = Client(firstName = faker.address().firstName(), lastName = faker.address().lastName())
    private fun randomProduct() = Product(code = faker.space().star(), color = faker.color().name())

}
