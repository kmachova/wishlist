package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.helper.CsvToProductConvertor
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.service.ClientService
import dk.cngroup.wishlist.service.ProductValidationService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ClientAddWishlistController(
    private val csvConverter: CsvToProductConvertor,
    private val productService: ProductValidationService,
    private val clientService: ClientService
) {
    @PostMapping("/clients/client-management/{username}/addWishlist")
    fun addWishListFromFile(
        @PathVariable username: String,
        @RequestPart csv: MultipartFile
    ): Client {
        val products = csvConverter.getProductsFromFile(csv)
        val validProducts = productService.getIfAllValid(products)
        val existingProducts = productService.getIfAllExist(validProducts)
        return clientService.addWishlistByUsername(username, existingProducts)
    }
}

