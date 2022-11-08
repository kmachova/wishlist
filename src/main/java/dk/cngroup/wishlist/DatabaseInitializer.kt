package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.*
import lombok.RequiredArgsConstructor
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class DatabaseInitializer(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val tieFighter = Product(code = "TIE Fighter")
        val deathStar = Product(code = "Death Star")
        val starDestroyer = Product(code = "Star Destroyer")
        val sand = Product(code = "sand")

        val wishlist3Products = Wishlist(products = arrayListOf(deathStar, starDestroyer, tieFighter))
        val wishlist2Products = Wishlist(products = arrayListOf(deathStar, starDestroyer))
        val wishlist1Product = Wishlist(products = arrayListOf(deathStar))


        val vader = Client(firstName = "Darth", lastName = "Vader")
        vader.addWishlist(wishlist3Products)

        val ren = Client(firstName = "Kylo", lastName = "Ren")
        ren.addWishlist(wishlist2Products)

        val skywalker = Client(firstName = "Luke", lastName = "Skywalker")
        skywalker.addWishlist(wishlist1Product)

        productRepository.save(sand)
        clientRepository.saveAll(arrayListOf(vader, skywalker, ren))

    }
}