package dk.cngroup.wishlist

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

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BaseSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ClientRepository clientRepository

    @Autowired
    ProductRepository productRepository

    protected static final PRODUCT_IN_ALL_WISHLISTS = 'Death Star'

    private createProduct(String code) { new Product(null, code) }

    private createClient(String firstName, String lastName, List<Wishlist> wishlist = []) {
        new Client(null, true, firstName, lastName, wishlist)
    }

    private def tieFighter = createProduct("TIE Fighter")
    private def deathStar = createProduct(PRODUCT_IN_ALL_WISHLISTS)
    private def starDestroyer = createProduct("Star Destroyer")
    private def sand = createProduct("sand")

    protected wishlist3Products = new Wishlist(products: [deathStar, starDestroyer, tieFighter])
    protected wishlist2Products = new Wishlist(products: [deathStar, starDestroyer])
    protected wishlist1Product = new Wishlist(products: [deathStar])

    protected vader = createClient("Darth", "Vader")
    private ren = createClient("Kylo", "Ren")
    private skywalker = createClient("Luke", "Skywalker")

    def fullSetup() {
        vader.addWishlist(wishlist3Products)
        ren.addWishlist(wishlist2Products)
        skywalker.addWishlist(wishlist1Product)
        clientRepository.saveAll([vader, ren, skywalker])
        productRepository.save(sand)
    }
}