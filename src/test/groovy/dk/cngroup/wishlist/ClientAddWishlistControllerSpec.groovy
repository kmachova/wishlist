package dk.cngroup.wishlist

import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.ResponseStatusException
import spock.lang.Shared

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClientAddWishlistControllerSpec extends BaseSpec {
    private static final FILE_PARAM = 'csv'
    private static final ADD_WISHLIST_TEMPLATE = "/clients/client-management/{username}/addWishlist"

    @Shared
    private final addWishlistPath = pathFromTemplate(ADD_WISHLIST_TEMPLATE, VADER_USERNAME)

    private final minimalCsvFile = mockCsvFile(DEATH_STAR_CODE)

    def 'should add wishlist to client: #name'() {
        given:
        clientRepository.save(vader)
        productRepository.saveAll([deathStar, starDestroyer, tieFighter])
        clearEntityManager()

        def file = mockCsvFile(productInfo)

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(file))

        then:
        def response = results
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        and: 'wishlist was saved and linked to correct client'
        clientRepository.findByUserName(VADER_USERNAME).wishes[0].products.with {
            collect { product -> product.code } == [DEATH_STAR_CODE, STAR_DESTROYER_CODE, TIE_FIGHTER_CODE]
            collect { product -> product.color } == ['black', null, null]
        }

        and: 'response contains client with new wishlist in correct format'
        assertThatJson(response)
                .isEqualTo(VADER_JSON)

        where:
        name                                    | productInfo
        'codes only'                            | "$DEATH_STAR_CODE\n$STAR_DESTROYER_CODE\n$TIE_FIGHTER_CODE"
        'colors - skip value for non-existing'  | "$DEATH_STAR_CODE,black\n$STAR_DESTROYER_CODE\n$TIE_FIGHTER_CODE"
        'colors - blank value for non-existing' | "$DEATH_STAR_CODE,black\n$STAR_DESTROYER_CODE,\t \t \n$TIE_FIGHTER_CODE, "
        'colors - empty value for non-existing' | "$DEATH_STAR_CODE,black\n$STAR_DESTROYER_CODE,\n$TIE_FIGHTER_CODE,"
        'when empty lines are present'          | "\n\n$DEATH_STAR_CODE\n\n$STAR_DESTROYER_CODE\n$TIE_FIGHTER_CODE\n"
    }

    def 'should not override existing wishlists'() {
        given: 'client with 2 wishlists'
        vader.addWishlist(wishlist2Products)
        vader.addWishlist(wishlist3Products)
        clientRepository.save(vader)

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(minimalCsvFile))

        then: 'number of wishlists was increased by 1'
        results
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.wishes', hasSize(3)))
    }

    def 'should fail when file with wishes is empty'() {
        given:
        def productCodesCsv =
                mockCsvFile("")

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof ResponseStatusException
        exception.message == errorMessage400('File with wishes is empty')
    }

    def 'should fail when some of rows contain too many columns'() {
        given:
        def randomColumnsN = 15
        def randomColumns = (0..<randomColumnsN).collect { FAKER.space().starCluster() }.join(',')
        def productCodesCsv =
                mockCsvFile("$DEATH_STAR_CODE,black\n$STAR_DESTROYER_CODE,,\n$TIE_FIGHTER_CODE$randomColumns")

        def expectedMessage = "Some of csv lines are invalid: [Line 2: Too many columns (3). Maximum is: 2., " +
                "Line 3: Too many columns ($randomColumnsN). Maximum is: 2.]"

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof InvalidCsvLinesException
        exception.message == errorMessage400(expectedMessage)

    }

    def 'should fail when product code is: #name'() {
        given:
        def expectedMessage = "Some of csv lines are invalid: [Line 2: Field 'code' is mandatory but no value was provided., " +
                "Line 4: Field 'code' is mandatory but no value was provided.]"

        def productCodesCsv = mockCsvFile(fileContent)

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof InvalidCsvLinesException
        exception.message == errorMessage400(expectedMessage)

        where:
        name    | fileContent
        'empty' | "${randomProductCode()}\n,\n${randomProductCode()}\n,\n${}\n${randomProductCode()}"
        'blank' | "${randomProductCode()}\n\t,\n${randomProductCode()}\n  \n${randomProductCode()}\n${randomProductCode()}"
    }

    def 'should fail when some of product codes are not found in product repo'() {
        given:
        productRepository.saveAllAndFlush([deathStar, starDestroyer, tieFighter])
        def nonExistingProductCodes = (0..<2).collect { randomProductCode() }

        def expectedMessage = "Wishlist was not created since some of products specified in the file do not exist: " +
                "[Line 3: code=${nonExistingProductCodes[0]}, color=null, Line 5: code=${nonExistingProductCodes[1]}, color=null]"

        def productCodesCsv = mockCsvFile([
                DEATH_STAR_CODE,
                TIE_FIGHTER_CODE,
                nonExistingProductCodes[0],
                STAR_DESTROYER_CODE,
                nonExistingProductCodes[1]
        ].join('\n'))

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof InvalidProductCodesInFileException
        exception.message == errorMessage400(expectedMessage)
    }

    def 'should fail when some of products have non-matching color'() {
        given:
        productRepository.saveAll([deathStar, starDestroyer, tieFighter])

        def anyColor = FAKER.color().name()
        def productCodesCsv = mockCsvFile(
                "$STAR_DESTROYER_CODE\n$DEATH_STAR_CODE,pink\n$TIE_FIGHTER_CODE,$anyColor")

        def expectedMessage = "Wishlist was not created since some of products specified in the file do not exist: " +
                "[Line 2: code=$DEATH_STAR_CODE, color=pink, Line 3: code=$TIE_FIGHTER_CODE, color=$anyColor]"

        when:
        def results = mockMvc.perform(multipart(addWishlistPath)
                .file(productCodesCsv))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof InvalidProductCodesInFileException
        exception.message == errorMessage400(expectedMessage)
    }

    def 'should fail when client does not exist'() {
        given:
        productRepository.saveAll([deathStar, starDestroyer, tieFighter])

        def nonExistingUsername = randomUserName()

        def path = pathFromTemplate(ADD_WISHLIST_TEMPLATE, nonExistingUsername)

        when:
        def results = mockMvc.perform(multipart(path)
                .file(minimalCsvFile))

        then:
        def exception = results
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof ClientUsernameNotFoundException
        exception.message == errorMessage404("Client with username '$nonExistingUsername' does not exist")
    }

    def 'should fail when productCode param is #name'() {
        when:
        def results = mockMvc.perform(multipart(path))

        then:
        def exception = results
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResolvedException()

        and:
        exception instanceof MissingServletRequestPartException
        exception.message == "Required request part 'csv' is not present"

        where:
        name      | path
        'missing' | addWishlistPath
        'flag'    | "$addWishlistPath?$FILE_PARAM"
    }

    def mockCsvFile(String content) {
        new MockMultipartFile('csv', null, 'text/csv', content.getBytes())
    }
}
