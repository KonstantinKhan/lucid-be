rootProject.name = "lucid-be"

include("lucid-be-ktor-app")
include("lucid-be-transport-openapi")
include("lucid-be-common")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
