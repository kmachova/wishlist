import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    val kotlinPluginVersion = "1.8.21"

    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.jpa") version kotlinPluginVersion
    kotlin("plugin.spring") version kotlinPluginVersion
    groovy

    id("io.gitlab.arturbosch.detekt") version ("1.22.0")
    codenarc
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "dk.cngroup.wishlist"
version = "1.0.0'"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi:2.1.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    implementation("com.google.code.gson:gson")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
//    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // dependencies for using Spock
    testImplementation("org.spockframework:spock-spring:2.4-M1-groovy-4.0")
    testImplementation("org.hamcrest:hamcrest-core:2.2") // only necessary if Hamcrest matchers are used
    testRuntimeOnly("net.bytebuddy:byte-buddy:1.12.23") // allows mocking of classes (in addition to interfaces)
}

kotlin {
    jvmToolchain(17)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

springBoot {
    buildInfo()
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("wishlist.jar")
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

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {

    ignoreFailures.set(false)
    //disabledRules.set(setOf("no-wildcard-imports"))
}

