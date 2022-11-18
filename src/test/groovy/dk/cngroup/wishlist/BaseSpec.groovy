package dk.cngroup.wishlist

import com.github.javafaker.Faker
import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import javax.persistence.EntityManager

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BaseSpec extends Specification implements TestUtils {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ClientRepository clientRepository

    @Autowired
    ProductRepository productRepository

    @Autowired
    EntityManager entityManager

    protected static final FAKER = new Faker()
    protected static final PRODUCT_IN_ALL_WISHLISTS = 'Death Star'
    protected static final VADER_USERNAME = 'DARTH_VADER'

    protected static final VADER_JSON = new File('src/test/resources/responses/DarthVaderWithWishlists.json').text


    protected createProduct(String code, String color = null) { new Product(null, code, color) }

    private createClient(String firstName, String lastName, List<Wishlist> wishlist = []) {
        new Client(null, true, firstName, lastName, wishlist)
    }

    protected tieFighter = createProduct("TIE Fighter")
    protected deathStar = createProduct(PRODUCT_IN_ALL_WISHLISTS, 'black')
    protected starDestroyer = createProduct("Star Destroyer")
    private sand = createProduct("sand")

    protected wishlist3Products = new Wishlist(products: [deathStar, starDestroyer, tieFighter])
    protected wishlist2Products = new Wishlist(products: [deathStar, starDestroyer])
    protected wishlist1Product = new Wishlist(products: [deathStar])

    protected vader = createClient("Darth", "Vader")
    private ren = createClient("Kylo", "Ren")
    protected skywalker = createClient("Luke", "Skywalker")

    def clearEntityManager() {
        entityManager.flush()
        entityManager.clear()
    }

    def fullSetup() {
        vader.addWishlist(wishlist3Products)
        ren.addWishlist(wishlist2Products)
        skywalker.addWishlist(wishlist1Product)
        clientRepository.saveAll([vader, ren, skywalker])
        productRepository.save(sand)
        clearEntityManager()
    }

    String randomProductCode() {
        FAKER.bothify("non-exist code ${'#'.repeat(10)}${'?'.repeat(10)}${'#'.repeat(10)}")
    }

    String randomUserName() {
        FAKER.letterify("${'?'.repeat(20)}_${'?'.repeat(10)}", true)
    }
}