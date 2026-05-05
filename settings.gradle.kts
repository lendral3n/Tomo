pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TomoSensei"

include(":app")

include(":core:common")
include(":core:design-system")
include(":core:data")
include(":core:srs")
include(":core:content")

include(":feature:drill")
include(":feature:stats")
include(":feature:chat")
include(":feature:onboarding")
include(":feature:photo")

include(":core:ai")

include(":service:gate-engine")
include(":service:overlay")
