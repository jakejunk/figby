import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "11"
        }
    }
}

tasks {
    wrapper {
        gradleVersion = "6.3"
    }
}
