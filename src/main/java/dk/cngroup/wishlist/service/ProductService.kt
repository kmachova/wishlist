package dk.cngroup.wishlist.service

import com.fasterxml.jackson.annotation.JsonProperty
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.exception.InvalidProductCodesInFileException
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    val productExampleMatcher = ExampleMatcher.matchingAll()
        .withIgnoreCase()
        .withIgnoreNullValues()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)

    fun checkExistenceByExample(productList: List<Product>): List<Product> {
        val splitProducts = productList
            .splitByPresenceInRepo()
        if (splitProducts.allValid) {
            return splitProducts.passedProducts
        } else throw InvalidProductCodesInFileException(splitProducts.failedProducts)
    }

    private fun List<Product>.splitByPresenceInRepo(): ProductsFromCsv {
        val invalidProducts = mutableListOf<FailedProduct>()
        val validProducts = mutableListOf<Product>()

        this.forEachIndexed { index, product ->
            if (product.color.isNullOrBlank()) {
                product.color = null
            }

            val example = Example.of(product, productExampleMatcher)
            val results = productRepository.findAll(example)

            when (results.size) {
                0 -> invalidProducts.add(FailedProduct(index + 1, product))
                else -> validProducts.add(results[0])
            }
        }
        return ProductsFromCsv(validProducts, invalidProducts)
    }
}

data class ProductsFromCsv(
    val passedProducts: MutableList<Product>,
    val failedProducts: List<FailedProduct>,
    val allValid: Boolean = failedProducts.isEmpty()
)

data class FailedProduct(
    @JsonProperty("line")
    val lineNumber: Int,
    val product: Product
)
