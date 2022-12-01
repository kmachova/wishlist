package dk.cngroup.wishlist.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.exceptions.CsvException
import dk.cngroup.wishlist.dto.ProductDto
import dk.cngroup.wishlist.dto.toEntity
import dk.cngroup.wishlist.exception.InvalidProductCodesInFileExceptionCsvWishesImportException
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import dk.cngroup.wishlist.exception.InvalidCsvLinesException
import dk.cngroup.wishlist.exception.WishlistPublicException
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
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
            .map{product -> product.toEntity()}
            .splitByPresenceInRepo()
        if (productsFromFile.allValid) {
            return Wishlist(products = productsFromFile.passedProducts)
        } else throw InvalidProductCodesInFileExceptionCsvWishesImportException(productsFromFile.failedProducts)
    }

    fun getProductsFromFile(file: MultipartFile): List<ProductDto> {
        if (file.isEmpty) throw WishlistPublicException(HttpStatus.BAD_REQUEST, "File with wishes is empty")

        InputStreamReader(file.inputStream).buffered().use {
            return csvToProducts(it)
        }
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

    private fun csvToProducts(fileReader: BufferedReader?): List<ProductDto> =
        CsvToBeanBuilder<ProductDto>(fileReader)
            .withType(ProductDto::class.java)
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

    private fun CsvToBean<ProductDto>.checkAndParse(): List<ProductDto> {
        val products = this.parse()
        val exceptions = this.capturedExceptions

        if (exceptions.size == 0)
            return products else throw InvalidCsvLinesException(exceptions.extractBasicInfo())
    }

    fun List<CsvException>.extractBasicInfo(): List<CsvExceptionBasicInfo> =
        this.map {
            CsvExceptionBasicInfo(it.lineNumber, it.message ?: "Unknown")
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


data class CsvExceptionBasicInfo(
    @JsonProperty("line")
    val lineNumber: Long,
    val cause: String
)


