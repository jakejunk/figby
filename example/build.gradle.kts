plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClassName = "com.example.MainKt"
}
