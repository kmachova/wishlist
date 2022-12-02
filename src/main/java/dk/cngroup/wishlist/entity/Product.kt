package dk.cngroup.wishlist.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.opencsv.bean.CsvBindByPosition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.Description
import org.springframework.validation.annotation.Validated
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Entity
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Description("Unique name of the item")
    @field:NotBlank(message = "Product code can not be blank")
    @CsvBindByPosition(position = 0, required = true)
    val code: String,

    @field:Pattern(regexp = "[a-z-, ]+", message = "Color can contain only lowercase letters, dash, comma or space")
    @CsvBindByPosition(position = 1)
    var color: String? = null
) : AuditableEntity() {
    override
    fun toString(): String = "code=${this.code}, color=${this.color}"
}

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findFirstProductByCodeIgnoreCase(code: String): Product?
}
