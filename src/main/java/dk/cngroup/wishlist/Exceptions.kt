package dk.cngroup.wishlist

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class NotFoundException(message: String) : ResponseStatusException(
    HttpStatus.NOT_FOUND,
    message
)

open class BadRequestException(message: String) : ResponseStatusException(
    HttpStatus.BAD_REQUEST,
    message
)

class ClientUsernameNotFoundException(username: String) :
    NotFoundException("Client with username '$username' does not exist")

class ProductCodeNotFoundException(productCode: String) :
    NotFoundException("Product code '$productCode' specified in the query parameter does not exist")

class AllWishesAreInvalidException:  BadRequestException("Whishlist was not added since it contains invalid product codes only")

class WishesCsvUpdateException :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in uploading file with wishes")