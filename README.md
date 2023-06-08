# Darth Vader's wishlist

## Static code analysis gradle plugins demo
This branch is a demo of following gradle static code analysis plugins:
* [detekt](https://detekt.dev/) (checks best practices and conventions in kotlin files; usage: ```./gradlew detekt```)
* [CodeNarc](https://codenarc.org/) (checks best practices and conventions in groovy files; usage: ```./gradlew codenarcTest```)
* [ktlint](https://pinterest.github.io/ktlint/0.49.1/) (checks and autocorrects formatting of kotlin files; usage: ```./gradlew ktlintCheck``` to check all the files (runs subsequently ```ktlintKotlinScriptCheck``` and ```ktlintMainSourceSetCheck```) ```./gradlew ktLintFormat``` to autocorrect the issues if possible)

There are maven alternatives for all of them.

The basic configuration is in [build.gradle.kts](build.gradle.kts). 
Configuration files for maven and detekt are [here](config).

Issues found by these plugins were not fixed. Moreover some other issues were added on purpose. 
Therefore the build fails unless you fix these issues or change the configuration of plugin.
You can build the branch without analysis by these plugins using ```./gradlew clean build -x detekt -x CodenarcTest -x ktlintKotlinScriptCheck -x ktlintMainSourceSetCheck```

## About the app

Kotlin/[Spock](https://spockframework.org/spock/docs/) demo app illustrating usage of various Spring, JPA and Hibernate features:

* **@EntityGraph** - EAGER loading of selected attributes ([ClientRepository](src/main/kotlin/dk/cngroup/wishlist/entity/Client.kt))
* **JPA auditing** - automatic insertion of useful entity stuff ([AuditableEntity](src/main/kotlin/dk/cngroup/wishlist/entity/AuditableEntity.kt))
* **@PrePersist** - adds new behavior to Entity before save happens ([AuditableEntity](src/main/kotlin/dk/cngroup/wishlist/entity/AuditableEntity.kt))
* **@Where** - allows soft deletes and other permanent filtering of entities ([Client](src/main/kotlin/dk/cngroup/wishlist/entity/Client.kt))
* **@Formula** - Hibernate computes virtual read-only column value using given expression ([Client](src/main/kotlin/dk/cngroup/wishlist/entity/Client.kt))
* **@OrderColumn** - allows preserving collection order even after save/load ([Wishlist](src/main/kotlin/dk/cngroup/wishlist/entity/Wishlist.kt))
* **Spring Data REST** - automatic exposure of Spring Data repositories via REST API ([spring-boot-starter-data-rest](build.gradle.kts) dependency)
* **@RepositoryRestController** - enhances Spring Data REST API by custom behavior ([ClientController](src/main/kotlin/dk/cngroup/wishlist/controller/ClientController.kt))

Project uses **H2 database** (can be [switched to dedicated MySQL](src/main/resources/application.yml)) initialized with [sample data](src/main/kotlin/dk/cngroup/wishlist/DatabaseInitializer.kt).

Useful runtime URLs:
* **[Swagger UI](http://localhost:8080/openapi/swagger)**
* **[Spring Data REST API](http://localhost:8080)**
* [Spring Data ALPS descriptors](http://localhost:8080/profile)

