package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.service.WishlistService
import dk.cngroup.wishlist.controller.ClientController.Companion.CLIENT_RESOURCE_PATH
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

class WishlistController(private val service: WishlistService) {
    @PostMapping("/$CLIENT_RESOURCE_PATH/addWishlist")
    fun addWishListFromFile(
        @PathVariable username: String,
        @RequestParam csv: MultipartFile
    ): Wishlist = service.readWishlistFromCsv(csv)

}