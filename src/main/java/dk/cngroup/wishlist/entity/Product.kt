package dk.cngroup.wishlist.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.Description
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Description("Unique name of the item")
    @field:NotNull
    val code: String
) : AuditableEntity()

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findFirstProductByCode(code: String): Product?
}

class ProductCodeNotFoundException(productCode: String) :
    ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Product code '$productCode' specified in the query parameter does not exist"
    )