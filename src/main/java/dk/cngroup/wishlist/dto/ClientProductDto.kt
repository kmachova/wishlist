package dk.cngroup.wishlist.dto

import dk.cngroup.wishlist.entity.Client
import org.mapstruct.Mapper
import org.mapstruct.Mapping

class ClientProductDto(
//    val productId: Long,
//    val productCode: String,
//    val productColor: String,
    val clientId: Long,
    val firstName: String,
    val lastName: String
) {
    // val id = "C${clientId}_P${productId}"
}

@Mapper
interface ClientToDtoMapper {

    @Mapping(target = "clientId", source = "id")
    fun entityToDto(client: Client): ClientProductDto

//    @Mapping(target = "id", source = "clientId")
//    fun entityToDto(dto: ClientProductDto): Client


}