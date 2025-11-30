plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    // kotlinx.serialization for JSON
    implementation(libs.kotlinx.serialization.json)

    // kotlinx-datetime for time handling
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.kotlin.test.junit)
}