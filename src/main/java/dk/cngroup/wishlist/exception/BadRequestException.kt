package dk.cngroup.wishlist.exception

import com.fasterxml.jackson.annotation.JsonProperty
import dk.cngroup.wishlist.dto.ProductValidationDto
import org.springframework.http.HttpStatus
import javax.validation.ConstraintViolation

open class BadRequestException(message: String, parameters: List<Any> = emptyList()) : WishlistPublicException(
    HttpStatus.BAD_REQUEST,
    message,
    parameters
)

class InvalidSorByException(invalidValues: List<String>, allowedValues: List<String>) : BadRequestException(
    "Result can not be sort by following properties: ${invalidValues.map { "'$it'" }}. " +
            "Allowed are ${allowedValues.map { "'$it'" }}."
)

class InvalidProductInBodyException(constraintViolations: Set<ConstraintViolation<*>>) : BadRequestException(
    "The product in the request body is invalid.", constraintViolations.map { it.message }
)

open class CsvWishesImportException(message: String, parameters: List<Any>) : BadRequestException(
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
