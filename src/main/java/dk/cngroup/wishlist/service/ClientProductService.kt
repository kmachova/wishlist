package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.exception.InvalidSorByException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.reflect.full.memberProperties

@Service
class ClientProductService {

    fun validateSort(sort: Sort) {
        val possibleValues = ClientProductDto::class.memberProperties.map { it.name }
        val invalidProperties = sort
            .map { it.property }
            .filterNot { possibleValues.contains(it) }

        if (invalidProperties.isNotEmpty()) throw InvalidSorByException(invalidProperties, possibleValues)
    }

}
