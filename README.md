# Darth Vader's wishlist

<p>Demo app illustrating usage of various Spring, JPA and Hibernate features:</p>

* **@EntityGraph** - EAGER loading of selected attributes ([ClientRepository](src/main/java/dk/cngroup/wishlist/entity/Client.kt))
* **JPA auditing** - automatic insertion of useful entity stuff ([AuditableEntity](src/main/java/dk/cngroup/wishlist/entity/AuditableEntity.kt))
* **@PrePersist** - adds new behavior to Entity before save happens ([AuditableEntity](src/main/java/dk/cngroup/wishlist/entity/AuditableEntity.kt))
* **@Where** - allows soft deletes and other permanent filtering of entities ([Client](src/main/java/dk/cngroup/wishlist/entity/Client.kt))
* **@Formula** - Hibernate computes virtual read-only column value using given expression ([Client](src/main/java/dk/cngroup/wishlist/entity/Client.kt))
* **@OrderColumn** - allows preserving collection order even after save/load ([Wishlist](src/main/java/dk/cngroup/wishlist/entity/Wishlist.kt))
* **Spring Data REST** - automatic exposure of Spring Data repositories via REST API ([spring-boot-starter-data-rest](build.gradle.kts) dependency)
* **@RepositoryRestController** - enhances Spring Data REST API by custom behavior ([ClientController](src/main/java/dk/cngroup/wishlist/controller/ClientController.kt))

Project uses **H2 database** (can be [switched to dedicated MySQL](src/main/resources/application.properties)) initialized with [sample data](src/main/java/dk/cngroup/wishlist/DatabaseInitializer.kt).

Useful runtime URLs:
* **[Swagger UI](http://localhost:8080/openapi/swagger)**
* **[Spring Data REST API](http://localhost:8080)**
* [Spring Data ALPS descriptors](http://localhost:8080/profile)

