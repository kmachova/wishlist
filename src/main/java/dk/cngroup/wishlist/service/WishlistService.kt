package dk.cngroup.wishlist.service

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import dk.cngroup.wishlist.WishesCsvUpdateException
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class WishlistService(private val productRepository: ProductRepository) {

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File with wishes is empty")
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

    fun readWishlistFromCsv(file: MultipartFile): Wishlist {
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null
        try {
            fileReader = BufferedReader(InputStreamReader(file.inputStream))
            val csvToBean = csvToProducts(fileReader).parse()
                .filterNot { productRepository.findFirstProductByCodeIgnoreCase(it.code) == null }
                .toMutableList()
            return Wishlist(products = csvToBean)
        } catch (ex: Exception) {
            throw WishesCsvUpdateException()
        } finally {
            closeFileReader(fileReader)
        }
    }
}