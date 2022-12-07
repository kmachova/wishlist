package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.entity.ClientRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientProductController(
    private val repository: ClientRepository
) {
    @GetMapping("/clients_products")
    fun getAllClientProductCombinations(): List<ClientProductDto> = repository.findAllClientProduct()

}
