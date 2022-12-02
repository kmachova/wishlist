package dk.cngroup.wishlist.helper

import dk.cngroup.wishlist.entity.Product

fun String.convert(): String =
    this
        .replace("\\s+".toRegex(), " ")
        .trim()

fun Product.covertInputValues() = Product(
    code = this.code.convert(),
    color = if(this.color.isNullOrBlank()) null else this.color?.convert()
)
