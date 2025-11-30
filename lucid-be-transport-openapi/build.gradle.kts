import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("org.openapi.generator") version "7.7.0"
}

group = "com.khan366kos"
version = "0.0.1"

dependencies {
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.jsr310)

    // Testing dependencies
    testImplementation(libs.kotlin.test.junit)
}

val openApiOutputDir = layout.buildDirectory.dir("generated/openapi")
val specsDir = "${rootDir.path}/specs"
val bundledSpec = "$specsDir/bundled-openapi.yaml"

// Task 1: Install npm dependencies
tasks.register<Exec>("npmInstall") {
    group = "openapi"
    description = "Install npm dependencies including @redocly/cli"

    workingDir = rootDir
    commandLine("npm", "install")

    inputs.file("${rootDir}/package.json")
    outputs.dir("${rootDir}/node_modules")
}

// Task 2: Bundle OpenAPI specs
tasks.register<Exec>("bundleOpenApi") {
    group = "openapi"
    description = "Bundle modular OpenAPI specs into a single file"

    dependsOn("npmInstall")

    workingDir = rootDir
    commandLine(
        "npx",
        "@redocly/cli",
        "bundle",
        "$specsDir/openapi.yaml",
        "--output", bundledSpec,
        "--ext", "yaml"
    )

    inputs.files(
        fileTree(specsDir) {
            include("**/*.yaml")
            exclude("bundled-openapi.yaml")
        }
    )
    outputs.file(bundledSpec)
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set(bundledSpec)
    outputDir.set(openApiOutputDir.get().asFile.absolutePath)
    packageName.set("com.khan366kos.transport")
    apiPackage.set("com.khan366kos.transport.api")
    modelPackage.set("com.khan366kos.transport.model")

    globalProperties.set(
        mapOf(
            "models" to "",
            "modelDocs" to "false",
            "apis" to "false",
            "apiDocs" to "false",
            "supportingFiles" to "false"
        )
    )

    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "original"
        )
    )
}

tasks.named("openApiGenerate").configure {
    dependsOn("bundleOpenApi")
    outputs.upToDateWhen { false }
    doFirst {
        delete(openApiOutputDir.get().asFile)
    }
    doLast {
        // Remove empty infrastructure folder
        val infraDir = file("${openApiOutputDir.get().asFile}/src/main/kotlin/com/khan366kos/transport/infrastructure")
        if (infraDir.exists() && infraDir.listFiles()?.isEmpty() == true) {
            delete(infraDir)
        }
    }
}

// Make sure generated sources are included in compilation
tasks.compileKotlin {
    dependsOn(tasks.named("openApiGenerate"))
}

tasks.processResources {
    dependsOn(tasks.named("openApiGenerate"))
}

// Configure source sets to include generated sources
sourceSets {
    main {
        java.srcDir(openApiOutputDir.map { it.dir("src/main/kotlin") })
        resources.srcDir(openApiOutputDir.map { it.dir("src/main/resources") })
    }
}
