package dk.cngroup.wishlist.helper

import com.github.javafaker.Faker
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.Wishlist
import dk.cngroup.wishlist.repository.ClientRepository
import dk.cngroup.wishlist.repository.ProductRepository

class RandomDataGenerator private constructor() {

    companion object {
        private val faker = Faker()
        private const val DEFAULT_RANDOM_DATA_COUNT = 10

        private fun randomClient() = Client(
            firstName = faker.address().firstName(), lastName = faker.address().lastName()
        )

        private fun randomProduct() = Product(code = faker.space().star(), color = faker.color().name())

        @JvmStatic
        fun ProductRepository.saveRandomProduct(nProducts: Int = DEFAULT_RANDOM_DATA_COUNT) =
            repeat(nProducts) {
                val product = Product(code = faker.space().moon(), color = faker.color().name())
                this.save(product)
            }

        @JvmStatic
        fun ClientRepository.saveRandomClients(nClients: Int = DEFAULT_RANDOM_DATA_COUNT) =
            repeat(nClients) {
                this.save(randomClient())
            }

        @JvmStatic
        fun ClientRepository.saveRandomClientsWithWishlists(
            nClients: Int = DEFAULT_RANDOM_DATA_COUNT,
            nProductInWishlists: List<IntRange> = listOf((1..5), (1..2))
        ) {
            repeat(nClients) {

                val client = randomClient()

                nProductInWishlists.map {
                    val products = (it).map { randomProduct() }.toMutableList()
                    client.addWishlist(Wishlist(products = products))
                }

                this.save(client)
            }
        }
    }

}
