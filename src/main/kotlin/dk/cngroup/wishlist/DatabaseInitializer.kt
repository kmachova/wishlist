package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DatabaseInitializer(private val clientRepository: ClientRepository) : CommandLineRunner {
    override fun run(vararg args: String) {

        val tieFighter =    Product(22)
        val deathStar = Product(434)
        val starDestroyer = Product( 443 )

        val wishlist = Wishlist(products = arrayListOf(tieFighter, deathStar, starDestroyer))
        val VADER = Client(firstName = "Darth", lastName = "Vader")
        VADER.addWishlist(wishlist)
        clientRepository.save(VADER)
    }

    companion object{


        const val NUMBER_OF_PRODUCTS = 3
    }
}
