package dk.cngroup.wishlist

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ProductCodeNotFoundException(productCode: String) :
    ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Product code '$productCode' specified in the query parameter does not exist"
    )

class WishesCsvUpdateException() : ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in uploading file with wishes")