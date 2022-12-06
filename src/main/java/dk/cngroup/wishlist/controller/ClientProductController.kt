package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.service.ClientProductService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ClientProductController(
    private val  service: ClientProductService
) {
    @GetMapping("/clients_products")
    fun getAllClientProductCombinations(): List<ClientProductDto> =
        service.getAllClientProductCombinations()

}