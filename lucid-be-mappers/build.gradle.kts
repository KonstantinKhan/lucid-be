plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.khan366kos"
version = "0.0.1"

dependencies {
    // Domain models
    implementation(project(":lucid-be-common"))
    implementation(project(":lucid-be-transport-openapi"))

    // kotlinx-datetime for time conversions
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    useJUnitPlatform()
}
