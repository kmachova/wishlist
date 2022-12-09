package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.repository.ClientRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ClientProductController(
    private val repository: ClientRepository
) {

    companion object {
        const val DEFAULT_PAGE_SIZE = "5"
    }

    @GetMapping("/clients_products")
    fun getAllClientProductCombinations(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        pagedResourcesAssembler: PagedResourcesAssembler<ClientProductDto>
    ): ResponseEntity<PagedModel<EntityModel<ClientProductDto>>> {
        val dtoPage = repository.findAllClientProduct(PageRequest.of(page, size))
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(dtoPage))
    }

}
