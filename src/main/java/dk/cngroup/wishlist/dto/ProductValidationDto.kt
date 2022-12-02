package dk.cngroup.wishlist.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import dk.cngroup.wishlist.entity.Product

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder("line")
class ProductValidationDto(
    @JsonIgnore
    val index: Int,
    @JsonIgnore
    val passed: Boolean,
    val product: Product,
    val messages: List<String> = emptyList()
) {
    @JsonProperty("line")
    val lineNumber: Int = index + 1
}
