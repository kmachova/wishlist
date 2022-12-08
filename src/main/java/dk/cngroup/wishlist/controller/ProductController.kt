package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.repository.ProductRepository
import dk.cngroup.wishlist.exception.InvalidProductInBodyException
import dk.cngroup.wishlist.helper.covertInputValues
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.ConstraintViolationException

//classic Spring MVC controller
@Validated
@RestController
class ProductController(private val repository: ProductRepository) {

    @PostMapping("/product")
    fun saveProduct(@RequestBody product: Product): Product =
        try {
            repository.save(product.covertInputValues())
        } catch (e: ConstraintViolationException) {
            throw InvalidProductInBodyException(e.constraintViolations)
        }
}
