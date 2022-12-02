package dk.cngroup.wishlist.helper

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.exceptions.CsvException
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.exception.CsvExceptionBasicInfo
import dk.cngroup.wishlist.exception.InvalidCsvLinesException
import dk.cngroup.wishlist.exception.WishlistPublicException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class CsvToProductConvertor {

    companion object {
        const val MAX_COLUMN_NUMBER = 2
    }

    fun getProductsFromFile(file: MultipartFile): List<Product> {
        if (file.isEmpty) throw WishlistPublicException(HttpStatus.BAD_REQUEST, "File with wishes is empty")

        InputStreamReader(file.inputStream).buffered().use { reader ->
            return csvToProducts(reader)
                .map { it.covertInputValues() }
        }
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

    private fun checkNumberOfColumns(line: Array<String>): Boolean =
        (line.size <= MAX_COLUMN_NUMBER).also {
            if (!it) throw CsvException("Too many columns (${line.size}). Maximum is: ${MAX_COLUMN_NUMBER}.")
        }

    private fun CsvToBean<Product>.checkAndParse(): List<Product> {
        val products = this.parse()
        val exceptions = this.capturedExceptions

        if (exceptions.size == 0)
            return products else throw InvalidCsvLinesException(exceptions.extractBasicInfo())
    }

    private fun List<CsvException>.extractBasicInfo(): List<CsvExceptionBasicInfo> =
        this.map {
            CsvExceptionBasicInfo(it.lineNumber, it.message ?: "Unknown")
        }
}
