plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

apiValidation {
    // Only validate the spotlight library module
    ignoredProjects.addAll(listOf("demo", "testing"))

    // Validate public API for all supported platforms
    nonPublicMarkers.add("io.piscesbn.xcodebn.piscespotlight.InternalPiscesSpotlightApi")
}