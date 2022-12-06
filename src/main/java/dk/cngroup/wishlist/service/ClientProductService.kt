package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.dto.ClientProductDto
import dk.cngroup.wishlist.dto.ClientToDtoMapper
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class ClientProductService(
    private val repository: ClientRepository
) {

    fun getAllClientProductCombinations(): List<ClientProductDto> {
        val allClients = repository.findClientsWithAtLeastOneProduct()
        val mapper = Mappers.getMapper(ClientToDtoMapper::class.java)

        return allClients.map { mapper.entityToDto(it) }
    }

}
