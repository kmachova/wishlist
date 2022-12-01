package dk.cngroup.wishlist.dto

import dk.cngroup.wishlist.entity.Product

fun ProductDto.toEntity(): Product = Product(
    code = this.code.convert(),
    color = this.color?.convert()
)

private fun String.convert(): String =
    this
        .replace("\\s+".toRegex(), " ")
        .trim()
