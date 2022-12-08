package dk.cngroup.wishlist.repository

import dk.cngroup.wishlist.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findFirstProductByCodeIgnoreCase(code: String): Product?
}
