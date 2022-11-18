package dk.cngroup.wishlist.service

import com.opencsv.bean.CsvToBeanBuilder
import dk.cngroup.wishlist.InvalidCsvLinesException
import dk.cngroup.wishlist.InvalidProductCodesInFileException
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class ReadWishlistService(private val productRepository: ProductRepository) {
    fun getWishlistFromCsv(file: MultipartFile): Wishlist {
        val productsFromFile = getProductsFromFile(file)
            .splitByPresenceInRepo()
        if (productsFromFile.allValid) {
            return Wishlist(products = productsFromFile.productsFromRepo)
        } else throw InvalidProductCodesInFileException(productsFromFile.failedProductCodes)
    }

    fun getProductsFromFile(file: MultipartFile): List<Product> {
        if (file.isEmpty) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File with wishes is empty")

        InputStreamReader(file.inputStream).buffered().use {
            return csvToProducts(it)
        }
    }

    private fun List<Product>.splitByPresenceInRepo(): ProductsFromCsv {
        val invalidProductsCodes = mutableListOf<String>()
        val validProducts = mutableListOf<Product>()

        this.forEach {
            val result = productRepository.findFirstProductByCodeIgnoreCase(it.code)

            if (result == null) {
                invalidProductsCodes.add(it.code)
            } else {
                validProducts.add(result)
            }
        }
        return ProductsFromCsv(validProducts, invalidProductsCodes)
    }

    private fun csvToProducts(fileReader: BufferedReader?): List<Product> {
        val beans = CsvToBeanBuilder<Product>(fileReader)
            .withType(Product::class.java)
            .withThrowExceptions(false)
            .build()

        val products = beans.parse()
        val exceptions = beans.capturedExceptions

        if (exceptions.size == 0) {
            return products
        } else throw InvalidCsvLinesException(exceptions)
    }
}

data class ProductsFromCsv(
    val productsFromRepo: MutableList<Product>,
    val failedProductCodes: List<String>,
    val allValid: Boolean = failedProductCodes.isEmpty()
)
