package dk.cngroup.wishlist.repository

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.entity.Client
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RestResource

interface ClientRepository : JpaRepository<Client, Long> {
    @RestResource(exported = false)
    fun getByUserName(userName: String): Client?

    @RestResource(exported = false)
    @EntityGraph(attributePaths = ["wishes"])
    fun findByUserName(userName: String): Client?

    @EntityGraph(attributePaths = ["wishes.products"])
    fun findClientByUserName(userName: String): Client

    @EntityGraph(attributePaths = ["wishes.products"])
    fun findClientByIdIn(id: List<Long>): List<Client>

    @Query("select c.id from Client c join c.wishes w join w.products p where upper(p.code) = upper(:productCode)")
    fun findClientIdByProductCode(productCode: String): List<Long>

    @Query(
        "select distinct new $clientProductDto (p.id, p.code, p.color,c.id, c.firstName, c.lastName) " +
                "from Client c join c.wishes w join w.products p"
    )
    fun findAllClientProduct(pageable: Pageable): Page<ClientProductDto>

    companion object {
        const val clientProductDto = "dk.cngroup.wishlist.dto.ClientProductDto"
    }
}
