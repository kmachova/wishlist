package dk.cngroup.wishlist.service

import dk.cngroup.wishlist.dto.ProductValidationDto
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.exception.InvalidProductsFormFileException
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.stereotype.Service
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

@Service
class ProductValidationService(
    private val productRepository: ProductRepository
) {

    val productExampleMatcher = ExampleMatcher.matchingAll()
        .withIgnoreCase()
        .withIgnoreNullValues()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    fun getIfAllValid(productList: List<Product>): List<Product> =
        productList.mapIndexed { index, product ->

            val violations: Set<ConstraintViolation<Product>> = validator.validate(product)
            ProductValidationDto(
                index = index,
                passed = violations.isEmpty(),
                product = product,
                messages = violations.map { it.message }
            )
        }.passIfAllValid("Some products from file are invalid")

    fun getIfAllExist(productList: List<Product>): List<Product> =
        productList.mapIndexed { index, product ->
            val results = productRepository.findAll(product.toExample())
            val found = results.size > 0

            ProductValidationDto(
                index = index,
                passed = found,
                product = if (found) results[0] else product
            )

        }.passIfAllValid("Some products from file are absent from the database")

    private fun List<ProductValidationDto>.passIfAllValid(errorMessage: String): List<Product> {
        if (this.all { it.passed }) {
            return this.map { it.product }
        } else throw InvalidProductsFormFileException(
            this.filter { !it.passed },
            errorMessage
        )
    }

    fun Product.toExample(): Example<Product> = Example.of(this, productExampleMatcher)

}
