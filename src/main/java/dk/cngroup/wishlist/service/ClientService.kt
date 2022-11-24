package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.ClientUsernameNotFoundException
import dk.cngroup.wishlist.ProductCodeNotFoundException
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import javax.persistence.EntityManager

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository,
    private val readWishlistService: ReadWishlistService,
    private val entityManager: EntityManager
) {
    private fun getByUsername(username: String): Client = try {
        clientRepository.findClientByUserName(username)
    } catch (e: Exception) {
        throw ClientUsernameNotFoundException(username)
    }

    fun getByProductCode(productCode: String): List<Client> {
        productRepository.findFirstProductByCodeIgnoreCase(productCode) ?: throw ProductCodeNotFoundException(
            productCode
        )

        val clientIds = clientRepository.findDistinctByWishesProductsCodeIgnoreCaseOrderByUserName(productCode)
            ?.map { it.id }
            ?: emptyList()
        entityManager.clear()
        return clientRepository.findAllById(clientIds)
    }

    fun addWishListToClient(username: String, csv: MultipartFile): Client {
        val wishListFromFile = readWishlistService.getWishlistFromCsv(csv)
        val client = getByUsername(username)

        client.addWishlist(wishListFromFile)
        clientRepository.save(client)
        return client
    }
}