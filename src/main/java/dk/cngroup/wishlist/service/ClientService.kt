package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.exception.ClientUsernameNotFoundException
import dk.cngroup.wishlist.exception.ProductCodeNotFoundException
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.lang.NullPointerException
import javax.persistence.EntityManager

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository,
    private val readWishlistService: ReadWishlistService,
    private val entityManager: EntityManager
) {
    fun getByProductCode(productCode: String): List<Client> {
        productRepository.findFirstProductByCodeIgnoreCase(productCode) ?: throw ProductCodeNotFoundException(
            productCode
        )

        val clients = clientRepository.findDistinctByWishesProductsCodeIgnoreCaseOrderByUserName(productCode)
            ?.map {
                entityManager.refresh(it)
                return@map it
            } ?: emptyList()
        return clients
    }

    fun addWishListFromFileToClient(username: String, csv: MultipartFile): Client {
        val wishListFromFile = readWishlistService.getWishlistFromCsv(csv)
        val client = getByUsername(username)

        client.addWishlist(wishListFromFile)
        clientRepository.save(client)
        client.refreshIfNullUsername()
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

    private fun Client.refreshIfNullUsername() =
        this.userName ?: run {
            clientRepository.flush()
            entityManager.refresh(this)
        }
}
