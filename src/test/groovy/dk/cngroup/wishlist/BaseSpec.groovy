package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist
import dk.cngroup.wishlist.entity.WishlistRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import javax.persistence.EntityManager

import static dk.cngroup.wishlist.TestUtils.responseJsonToString
import static dk.cngroup.wishlist.TestUtils.FAKER

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings('StaticFieldsBeforeInstanceFields')
class BaseSpec extends Specification {

    @Autowired
    protected MockMvc mockMvc

    @Autowired
    protected ClientRepository clientRepository

    @Autowired
    protected ProductRepository productRepository

    @Autowired
    protected WishlistRepository wishlistRepository

    @Autowired
    protected EntityManager entityManager

    protected static final DEATH_STAR_CODE = 'Death Star'
    protected static final TIE_FIGHTER_CODE = 'TIE Fighter'
    protected static final STAR_DESTROYER_CODE = 'Star Destroyer'

    protected static final VADER_USERNAME = 'DARTH_VADER'
    protected static final VADER_JSON = responseJsonToString('DarthVaderWithWishesAndPlaceHolders')

    protected constructProduct(String code, String color = null) { new Product(null, code, color) }

    private constructClient(String firstName, String lastName, List<Wishlist> wishlist = []) {
        new Client(null, true, firstName, lastName, wishlist)
    }

    protected tieFighter = constructProduct(TIE_FIGHTER_CODE)
    protected deathStar = constructProduct(DEATH_STAR_CODE, 'black')
    protected starDestroyer = constructProduct(STAR_DESTROYER_CODE)
    private final sand = constructProduct('sand')

    protected wishlist3Products = new Wishlist(products: [deathStar, starDestroyer, tieFighter])
    protected wishlist2Products = new Wishlist(products: [deathStar, starDestroyer])
    protected wishlist1Product = new Wishlist(products: [deathStar])

    protected vader = constructClient('Darth', 'Vader')
    private final ren = constructClient('Kylo', 'Ren')
    protected skywalker = constructClient('Luke', 'Skywalker')

    protected void fullSetup() {
        vader.addWishlist(wishlist3Products)
        ren.addWishlist(wishlist2Products)
        skywalker.addWishlist(wishlist1Product)
        clientRepository.saveAll([vader, ren, skywalker])
        productRepository.save(sand)
    }

    def clearEntityManager() {
        entityManager.flush()
        entityManager.clear()
    }

    protected String randomProductCode() {
        FAKER.bothify("non-exist code ${'#'.repeat(10)}${'?'.repeat(10)}${'#'.repeat(10)}")
    }

    protected String randomUserName() {
        FAKER.letterify("${'?'.repeat(20)}_${'?'.repeat(10)}", true)
    }

}
