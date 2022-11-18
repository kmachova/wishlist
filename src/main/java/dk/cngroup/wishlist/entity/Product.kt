package dk.cngroup.wishlist.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvBindByPosition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.Description
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @CsvBindByPosition(position = 0, required = true)
    @Description("Unique name of the item")
    @field:NotNull
    val code: String,

    @CsvBindByPosition(position = 1)
    var color: String? = null
) : AuditableEntity()

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findFirstProductByCodeIgnoreCase(code: String): Product?
}