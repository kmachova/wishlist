package dk.cngroup.wishlist

import com.opencsv.exceptions.CsvException
import dk.cngroup.wishlist.entity.Product
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

class InvalidCsvLinesException(exceptions: List<CsvException>) :
    BadRequestException(
        "Some of csv lines are invalid: ${exceptions.map { "Line ${it.lineNumber}: ${it.message}" }}"
    )

class InvalidProductCodesInFileException(products: Map<Int, Product>) :
    BadRequestException(
        "Wishlist was not created since some of products specified in the file do not exist: " +
                products.map { "Line ${it.key}: ${it.value}" }
    )