package dk.cngroup.wishlist.exception

import dk.cngroup.wishlist.service.CsvExceptionBasicInfo
import dk.cngroup.wishlist.service.FailedProduct
import org.springframework.http.HttpStatus

open class CsvWishesImportException(message: String, parameters: List<Any>) : WishlistPublicException(
    HttpStatus.BAD_REQUEST,
    message,
    parameters
)

class InvalidCsvLinesExceptionCsvWishesImportException(exceptions: List<CsvExceptionBasicInfo>) :
    CsvWishesImportException(
        "Some of csv lines are invalid.",
        exceptions
    )

class InvalidProductCodesInFileExceptionCsvWishesImportException(failedProducts: List<FailedProduct>) :
    CsvWishesImportException(
        "Wishlist was not created since some of products specified in the file do not exist.",
        failedProducts
    )