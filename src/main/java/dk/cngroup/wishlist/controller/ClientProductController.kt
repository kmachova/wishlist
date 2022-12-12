package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.repository.ClientRepository
import dk.cngroup.wishlist.service.ClientProductService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientProductController(
    private val repository: ClientRepository,
    private val service: ClientProductService
) {

    @GetMapping("/clients_products")
    fun getAllClientProductCombinations(
        @PageableDefault(size = 5) pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<ClientProductDto>
    ): PagedModel<EntityModel<ClientProductDto>> {
        service.validateSort(pageable.sort)
        val dtoPage = repository.findAllClientProduct(pageable)
        return pagedResourcesAssembler.toModel(dtoPage)
    }

}

