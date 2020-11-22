import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    // TODO: Look into https://github.com/gladed/gradle-android-git-version as an alternative
    id("com.palantir.git-version") version "0.12.3"
    application
}

val gitVersion: groovy.lang.Closure<String> by extra
group = "dev.junker"
version = gitVersion()

kotlin {
    explicitApi()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:4.3.1")
    testImplementation("io.kotest:kotest-assertions-core:4.3.1")
    testImplementation("io.kotest:kotest-property:4.3.1")
}

application {
    mainClassName = "MainKt"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    withType<Test> {
        useJUnitPlatform()
    }
    wrapper {
        gradleVersion = "6.3"
    }
}