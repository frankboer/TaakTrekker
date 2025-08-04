// By default, buildSrc doesn't provide access to the parent build's version
// catalog.
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}