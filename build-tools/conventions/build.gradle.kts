plugins {
    `kotlin-dsl`
}

dependencies {
    // Makes 'libs' version catalog visible and type-safe for precompiled plugins.
    // https://github.com/gradle/gradle/issues/15383#issuecomment-1245546796
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    compileOnly(libs.gradlePlugin.mavenPublish)
}

gradlePlugin {
    plugins {
        val mavenPublish by registering {
            id = "mavenPublish"
            implementationClass = "MavenPublishPlugin"
        }
    }
}