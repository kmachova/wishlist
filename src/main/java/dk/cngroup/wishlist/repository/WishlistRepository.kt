package dk.cngroup.wishlist.repository

import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistRepository : JpaRepository<Wishlist?, Long?>
