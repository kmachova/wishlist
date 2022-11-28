package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.service.ClientService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

//classic Spring MVC controller
@RestController
class ClientSearchController(
    private val clientService: ClientService
) {

    @GetMapping("/clients/search")
    fun getByProduct(
        @RequestParam productCode: String
    ): List<Client> = clientService.getByProductCode(productCode)

}
