plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":figby"))
}

application {
    mainClassName = "com.example.MainKt"
}
