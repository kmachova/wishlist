package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.helper.CsvToProductDtoConverter
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.service.ClientService
import dk.cngroup.wishlist.service.ProductService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ClientAddWishlistController(
    private val csvConverter: CsvToProductDtoConverter,
    private val productService: ProductService,
    private val clientService: ClientService
) {
    @PostMapping("/clients/client-management/{username}/addWishlist")
    fun addWishListFromFile(
        @PathVariable username: String,
        @RequestPart csv: MultipartFile
    ): Client {
        val products = csvConverter.getProductsFromFile(csv)
        val validatedProducts = productService.checkExistenceByExample(products)
        return clientService.addWishlistByUsername(username, validatedProducts)
    }
}

