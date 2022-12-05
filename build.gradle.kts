import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("io.gitlab.arturbosch.detekt").version("1.17.0")
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.jpa") version "1.7.20"
    kotlin("plugin.spring") version "1.7.20"
    groovy
    codenarc
}

group = "dk.cngroup.wishlist"
version = "1.0.0'"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.12")
    implementation("org.springdoc:springdoc-openapi-data-rest:1.6.12")
    implementation("org.springdoc:springdoc-openapi-security:1.6.12")
    implementation("com.google.code.gson:gson")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
//    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // dependencies for using Spock
    implementation("org.codehaus.groovy:groovy")
    testImplementation("org.spockframework:spock-spring:2.3-groovy-3.0")
    testImplementation("org.hamcrest:hamcrest-core:2.2")   // only necessary if Hamcrest matchers are used
    testRuntimeOnly("net.bytebuddy:byte-buddy:1.12.18") // allows mocking of classes (in addition to interfaces)

    testImplementation("net.javacrumbs.json-unit:json-unit:2.36.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.36.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-spring:2.36.0")
    implementation("com.github.javafaker:javafaker:0.12")
    implementation("com.opencsv:opencsv:5.7.1")

    testImplementation("org.codehaus.groovy:groovy-templates:2.0.0")

    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

springBoot {
    buildInfo()
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("wishlist.jar")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

codenarc {
    toolVersion = "3.1.0"
    configFile = file("${rootProject.projectDir}/config/codenarc/ruleset")
    reportFormat = "html"
}

detekt {
    config = files("${rootProject.projectDir}/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

