package dk.cngroup.wishlist.controller

import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
@Slf4j
class RestExceptionHandler {

    @ExceptionHandler(EmptyResultDataAccessException::class)
    @ResponseStatus(NOT_FOUND)
    fun handleNotFoundException(e: Exception): String? {
        return e.message
    }

    @ExceptionHandler(MissingRequestValueException::class, MissingServletRequestPartException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleMissingRequestValue(e: Exception): String? {
        logger.error("Bad request - missing request value", e)
        return e.message
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: Exception) {
        logger.error("Failed with ResponseStatusException.")
        throw e
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleAnyException(t: Throwable): String? {
        logger.error("Internal server error", t)
        return t.message
    }
}

private val logger = KotlinLogging.logger {}