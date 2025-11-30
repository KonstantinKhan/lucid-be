rootProject.name = "lucid-be"

include("lucid-be-ktor-app")
include("lucid-be-transport-openapi")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
