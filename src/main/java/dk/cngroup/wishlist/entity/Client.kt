package dk.cngroup.wishlist.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import dk.cngroup.wishlist.dto.ClientProductDto
import org.hibernate.annotations.Formula
import org.hibernate.annotations.Where
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RestResource
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OrderColumn

@Entity //all SELECT statements will be enhanced by given where condition; cannot be inherited from parent class
@Where(clause = "active = true")
class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var active: Boolean = true,
    var firstName: String,
    var lastName: String,
    @JsonManagedReference
    @OneToMany(mappedBy = "client", cascade = [CascadeType.ALL])
    @OrderColumn
    var wishes: MutableList<Wishlist> = mutableListOf()
) {
    @Formula("upper(concat(first_name, '_', last_name))")
    var userName: String? = null

    fun addWishlist(wishlist: Wishlist) {
        wishes += wishlist
        wishlist.client = this
    }
}

