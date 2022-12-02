package dk.cngroup.wishlist.exception

import com.fasterxml.jackson.annotation.JsonProperty
import dk.cngroup.wishlist.dto.ProductValidationDto
import org.springframework.http.HttpStatus

open class CsvWishesImportException(message: String, parameters: List<Any>) : WishlistPublicException(
    HttpStatus.BAD_REQUEST,
    message,
    parameters
)

class InvalidCsvLinesException(exceptions: List<CsvExceptionBasicInfo>) :
    CsvWishesImportException(
        "Some of csv lines are invalid.",
        exceptions
    )

class InvalidProductsFormFileException(
    invalidProducts: List<ProductValidationDto>,
    message: String = "Some products obtained from file are invalid"
) :
    CsvWishesImportException(
        message,
        invalidProducts
    )

data class CsvExceptionBasicInfo(
    @JsonProperty("line")
    val lineNumber: Long,
    val cause: String
)

