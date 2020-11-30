val gitVersion: groovy.lang.Closure<String> by extra

plugins {
    kotlin("jvm")
    id("com.palantir.git-version") version "0.12.3"
}

group = "dev.junker"
version = gitVersion()

kotlin {
    explicitApi()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:4.3.1")
    testImplementation("io.kotest:kotest-assertions-core:4.3.1")
    testImplementation("io.kotest:kotest-property:4.3.1")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}