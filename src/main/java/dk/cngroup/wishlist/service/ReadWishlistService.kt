package dk.cngroup.wishlist.service

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.exceptions.CsvException
import dk.cngroup.wishlist.InvalidCsvLinesException
import dk.cngroup.wishlist.InvalidProductCodesInFileException
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class ReadWishlistService(private val productRepository: ProductRepository) {

    companion object {
        const val MAX_COLUMN_NUMBER = 2
    }

    val productExampleMatcher = ExampleMatcher.matchingAll()
        .withIgnoreCase()
        .withIgnoreNullValues()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)

    fun getWishlistFromCsv(file: MultipartFile): Wishlist {
        val productsFromFile = getProductsFromFile(file)
            .splitByPresenceInRepo()
        if (productsFromFile.allValid) {
            return Wishlist(products = productsFromFile.passedProducts)
        } else throw InvalidProductCodesInFileException(productsFromFile.failedProducts)
    }

    fun getProductsFromFile(file: MultipartFile): List<Product> {
        if (file.isEmpty) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File with wishes is empty")

        InputStreamReader(file.inputStream).buffered().use {
            return csvToProducts(it)
        }
    }

    private fun List<Product>.splitByPresenceInRepo(): ProductsFromCsv {
        val invalidProductsCodes = mutableMapOf<Int, Product>()
        val validProducts = mutableListOf<Product>()

        this.forEachIndexed { index, product ->
            if (product.color.isNullOrBlank()) {
                product.color = null
            }

            val example = Example.of(product, productExampleMatcher)
            val results = productRepository.findAll(example)

            when (results.size) {
                0 -> invalidProductsCodes[index + 1] = product
                else -> validProducts.add(results[0])
            }
        }
        return ProductsFromCsv(validProducts, invalidProductsCodes)
    }

    private fun csvToProducts(fileReader: BufferedReader?): List<Product> =
        CsvToBeanBuilder<Product>(fileReader)
            .withType(Product::class.java)
            .withIgnoreEmptyLine(true)
            .withFilter { line ->
                checkNumberOfColumns(line)
            }
            .withThrowExceptions(false)
            .withQuoteChar('"')
            .withIgnoreLeadingWhiteSpace(true)
            .build()
            .checkAndParse()

    private fun checkNumberOfColumns(line: Array<String>): Boolean = (line.size <= MAX_COLUMN_NUMBER).also {
        if (!it) throw CsvException("Too many columns (${line.size}). Maximum is: $MAX_COLUMN_NUMBER.")
    }

    private fun CsvToBean<Product>.checkAndParse(): List<Product> {
        val products = this.parse()
        val exceptions = this.capturedExceptions

        if (exceptions.size == 0) {
            return products
        } else throw InvalidCsvLinesException(exceptions)
    }
}

data class ProductsFromCsv(
    val passedProducts: MutableList<Product>,
    val failedProducts: Map<Int, Product>,
    val allValid: Boolean = failedProducts.isEmpty()
)
