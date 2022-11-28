package dk.cngroup.wishlist.exception

import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.support.MissingServletRequestPartException

@RestControllerAdvice
@Slf4j
class RestExceptionHandler {

    @ExceptionHandler(EmptyResultDataAccessException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: Exception): String? {
        return e.message
    }

    @ExceptionHandler(MissingRequestValueException::class, MissingServletRequestPartException::class)
    fun handleMissingRequestValue(e: Exception): ResponseEntity<ErrorBody> {
        logger.error("Bad request - missing request value", e)
        val e2 = WishlistPublicException(HttpStatus.BAD_REQUEST, e.message?: "missing request value")
        return handleResponseStatusException(e2)
    }

    @ExceptionHandler(WishlistPublicException::class)
    fun handleResponseStatusException(e: WishlistPublicException): ResponseEntity<ErrorBody>{
        logger.error("Failed with ${e.message}.")
        return ResponseEntity(e.body, e.status)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleAnyException(t: Throwable) {
        logger.error("Internal server error", t)
        throw WishlistPublicException(HttpStatus.INTERNAL_SERVER_ERROR, t.message?: "Internal server error")
    }
}

private val logger = KotlinLogging.logger {}
