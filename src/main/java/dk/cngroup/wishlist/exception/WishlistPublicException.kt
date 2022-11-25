package dk.cngroup.wishlist.exception

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class WishlistPublicException(status: HttpStatus, message: String, parameters: List<Any> = emptyList()) :
    ResponseStatusException(status, message) {
    val body = ErrorBody(
        this.status,
        this.status.value(),
        this.reason ?: "Request failed with $status",
        parameters
    )
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ErrorBody(
    val status: HttpStatus,
    val statusCode: Int,
    val message: String,
    val parameters: List<Any>
)