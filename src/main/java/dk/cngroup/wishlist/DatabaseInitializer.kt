package dk.cngroup.wishlist

import dk.cngroup.wishlist.helper.RandomDataGenerator.Companion.saveRandomClients
import dk.cngroup.wishlist.helper.RandomDataGenerator.Companion.saveRandomClientsWithWishlists
import dk.cngroup.wishlist.helper.RandomDataGenerator.Companion.saveRandomProduct
import dk.cngroup.wishlist.repository.ClientRepository
import dk.cngroup.wishlist.repository.ProductRepository
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

        productRepository.saveRandomProduct()
        clientRepository.saveRandomClients()
        clientRepository.saveRandomClientsWithWishlists()

    }

}
