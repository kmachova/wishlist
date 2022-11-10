package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import dk.cngroup.wishlist.entity.Wishlist

trait DBTestData {

    private createProduct(String code) { new Product(null, code) }

    private createClient(String firstName, String lastName, List<Wishlist> wishlist = []) {
        new Client(null, true, firstName, lastName, wishlist)
    }

    private def tieFighter = createProduct("TIE Fighter")
    private def deathStar = createProduct("Death Star")
    private def starDestroyer = createProduct("Star Destroyer")
    private def sand = createProduct("sand")

    private wishlist3Products = new Wishlist(products: [deathStar, starDestroyer, tieFighter])
    private wishlist2Products = new Wishlist(products: [deathStar, starDestroyer])
    private wishlist1Product = new Wishlist(products: [deathStar])

    private vader = createClient("Darth", "Vader")
    private ren = createClient("Kylo", "Ren")
    private skywalker = createClient("Luke", "Skywalker")

    def oneClientWithoutWishesSetup(ClientRepository clientRepository) {
        clientRepository.save(vader)
    }

    def oneClientWithWishesSetup(ClientRepository clientRepository) {
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)
    }

    def fullSetup(ClientRepository clientRepository, ProductRepository productRepository) {
        vader.addWishlist(wishlist3Products)
        ren.addWishlist(wishlist2Products)
        skywalker.addWishlist(wishlist1Product)
        clientRepository.saveAll([vader, ren, skywalker])
        productRepository.save(sand)
    }
}