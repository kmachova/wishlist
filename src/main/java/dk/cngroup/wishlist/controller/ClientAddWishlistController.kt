package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.ClientUsernameNotFoundException
import dk.cngroup.wishlist.service.ReadWishlistService
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class ClientAddWishlistController(
    private val clientRepository: ClientRepository,
    private val service: ReadWishlistService
) {
    @PostMapping("/clients/client-management/{username}/addWishlist")
    fun addWishListFromFile(
        @PathVariable username: String,
        @RequestPart csv: MultipartFile
    ): ResponseEntity<Wishlist> {
        val wishListFromFile = service.getWishlistFromCsv(csv)
        val client = try {
            clientRepository.findClientByUserName(username)
        } catch (e: Exception) {
            throw ClientUsernameNotFoundException(username)
        }
        client.addWishlist(wishListFromFile)
        clientRepository.save(client)
        return ResponseEntity<Wishlist>(wishListFromFile, HttpStatus.CREATED)
    }
}