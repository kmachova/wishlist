package dk.cngroup.wishlist.service

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import dk.cngroup.wishlist.InvalidProductCodeInFileException
import dk.cngroup.wishlist.WishesCsvUpdateException
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class ReadWishlistService(private val productRepository: ProductRepository) {
    fun getWishlistFromCsv(file: MultipartFile): Wishlist {
        val productsFromFile = getProductsFromFile(file)
        val filteredProducts = filterProducts(productsFromFile)
        return Wishlist(products = filteredProducts)
    }

    fun getProductsFromFile(file: MultipartFile): List<Product> {
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null
        try {
            fileReader = BufferedReader(InputStreamReader(file.inputStream))
            return csvToProducts(fileReader).parse()
        } catch (ex: Exception) {
            throw WishesCsvUpdateException()
        } finally {
            closeFileReader(fileReader)
        }
    }

    private fun filterProducts(products: List<Product>): MutableList<Product> {
        return products.map {
            productRepository.findFirstProductByCodeIgnoreCase(it.code)
                ?: throw InvalidProductCodeInFileException(it.code)
        }
            .toMutableList()
    }

    private fun csvToProducts(fileReader: BufferedReader?): CsvToBean<Product> =
        CsvToBeanBuilder<Product>(fileReader)
            .withType(Product::class.java)
            .withIgnoreLeadingWhiteSpace(true)
            .build()

    private fun closeFileReader(fileReader: BufferedReader?) {
        try {
            fileReader!!.close()
        } catch (ex: IOException) {
            throw WishesCsvUpdateException()
        }
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File with wishes is empty")
    }
}