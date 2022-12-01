package dk.cngroup.wishlist.dto

import com.opencsv.bean.CsvBindByPosition
import dk.cngroup.wishlist.entity.AuditableEntity
import org.springframework.data.rest.core.annotation.Description
import javax.validation.constraints.NotNull

class ProductDto(
    @CsvBindByPosition(position = 0, required = true)
    @Description("Unique name of the item")
    @field:NotNull
    val code: String,

    @CsvBindByPosition(position = 1)
    var color: String? = null
) : AuditableEntity() {
    override
    fun toString(): String = "code=${this.code}, color=${this.color}"
}
