package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.ProductRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FetchProductsTest {
    @Autowired
    var productRepository: ProductRepository? = null

    //check log to see the difference in SQL executed by Hibernate
    @Test
    fun testFindProductByCode() {
        productRepository!!.findFirstProductByCodeIgnoreCase("randomCode")
    }
}
