package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.ProductCodeNotFoundException
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.web.bind.annotation.*

//classic Spring MVC controller
@RestController
class ClientSearchController(
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository
) {

    @GetMapping("/clients/search")
    fun getByProduct(
        @RequestParam productCode: String
    ): List<Client> {
        productRepository.findFirstProductByCode(productCode) ?: throw ProductCodeNotFoundException(productCode)

        return clientRepository.findDistinctByWishesProductsCodeOrderByUserName(productCode) ?: emptyList()
    }
}