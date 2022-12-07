package dk.cngroup.wishlist.dto

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("id")
class ClientProductDto(
    val productId: Long,
    val productCode: String,
    val productColor: String,

    val clientId: Long,
    val firstName: String,
    val lastName: String
) {
    val id = "C${clientId}_P${productId}"
}
