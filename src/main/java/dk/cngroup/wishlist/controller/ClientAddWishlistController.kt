package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Wishlist
import dk.cngroup.wishlist.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class ClientAddWishlistController(
    private val clientService: ClientService
) {
    @PostMapping("/clients/client-management/{username}/addWishlist")
    fun addWishListFromFile(
        @PathVariable username: String,
        @RequestPart csv: MultipartFile
    ): ResponseEntity<Wishlist> {
        val wishList = clientService.addWishListToClient(username, csv)
        return ResponseEntity<Wishlist>(wishList, HttpStatus.CREATED)
    }
}