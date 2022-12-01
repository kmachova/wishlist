package dk.cngroup.wishlist.helper

fun String.convert(): String =
    this
        .replace("\\s+".toRegex(), " ")
        .trim()
