package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.service.ClientService
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
    ): Client = clientService.addWishListFromFileToClient(username, csv)
}