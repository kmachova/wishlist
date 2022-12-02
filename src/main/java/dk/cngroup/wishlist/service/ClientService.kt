package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.exception.ClientUsernameNotFoundException
import dk.cngroup.wishlist.exception.ProductCodeNotFoundException
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository
) {
    fun getByProductCode(productCode: String): List<Client> {
        productRepository.findFirstProductByCodeIgnoreCase(productCode) ?: throw ProductCodeNotFoundException(
            productCode
        )
        val clientId = clientRepository.findClientIdByProductCode(productCode)
        return clientRepository.findClientByIdIn(clientId)
    }

    fun addWishlistByUsername(username: String, products: List<Product>): Client {
        val wishlist = Wishlist(products = products.toMutableList())
        return addWishlistByUsername(username, wishlist)
    }

    fun addWishlistByUsername(username: String, wishlist: Wishlist): Client {
        val client = getByUsername(username)

        client.addWishlist(wishlist)
        clientRepository.save(client)
        return client
    }

    @Suppress("SwallowedException")
    private fun getByUsername(username: String): Client {
        val client = try {
            clientRepository.findClientByUserName(username)
        } catch (e: EmptyResultDataAccessException) {
            throw ClientUsernameNotFoundException(username)
        }
        return client
    }
}
