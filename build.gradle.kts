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

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation(kotlin("test-junit"))
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