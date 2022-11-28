package dk.cngroup.wishlist.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.Description
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OrderColumn

@Entity
class Wishlist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @JsonBackReference
    @Description("The user holding items in this wishlist")
    @ManyToOne
    var client: Client? = null,

    @OrderColumn
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @Description("A list of items added by the client")
    var products: MutableList<Product> = arrayListOf()
)

interface WishlistRepository : JpaRepository<Wishlist?, Long?>
