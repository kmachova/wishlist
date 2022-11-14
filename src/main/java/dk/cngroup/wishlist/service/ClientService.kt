package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.ClientUsernameNotFoundException
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val readWishlistService: ReadWishlistService
) {
    private fun getByUsername(username: String): Client = try {
        clientRepository.findClientByUserName(username)
    } catch (e: Exception) {
        throw ClientUsernameNotFoundException(username)
    }

    fun addWishListToClient(username: String, csv: MultipartFile): Wishlist {
        val wishListFromFile = readWishlistService.getWishlistFromCsv(csv)
        val client = getByUsername(username)

        client.addWishlist(wishListFromFile)
        clientRepository.save(client)
        return wishListFromFile
    }
}