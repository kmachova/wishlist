package dk.cngroup.wishlist.exception

import org.springframework.http.HttpStatus

open class NotFoundException(message: String) : WishlistPublicException(
    HttpStatus.NOT_FOUND,
    message
)

class ClientUsernameNotFoundException(username: String) :
    NotFoundException("Client with username '$username' does not exist")

class ProductCodeNotFoundException(productCode: String) :
    NotFoundException("Product code '$productCode' specified in the query parameter does not exist")