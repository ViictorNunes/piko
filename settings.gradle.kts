rootProject.name = "piko-twitter-patches"

buildCache {
    local {
        isEnabled = !System.getenv().containsKey("CI")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MorpheApp/registry")
            credentials {
                username = providers.gradleProperty("gpr.user")
                    .orNull ?: System.getenv("GPR_USER") 
                    ?: System.getenv("GITHUB_ACTOR") 
                    ?: "git"
                password = providers.gradleProperty("gpr.key")
                    .orNull ?: System.getenv("GPR_KEY") 
                    ?: System.getenv("GH_TOKEN") 
                    ?: System.getenv("GITHUB_TOKEN") 
                    ?: ""
            }
        }
    }
}

plugins {
    id("app.morphe.patches") version "1.3.3"
}

settings {
    extensions {
        defaultNamespace = "app.morphe.extension"

        // Must resolve to an absolute path (not relative),
        // otherwise the extensions in subfolders will fail to find the proguard config.
        proguardFiles(rootProject.projectDir.resolve("extensions/proguard-rules.pro").toString())
    }
}
