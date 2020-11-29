plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClassName = "MainKt"
}
