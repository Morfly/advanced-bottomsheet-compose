import io.morfly.buildtools.ConventionPlugin
import io.morfly.buildtools.libs
import io.morfly.buildtools.mavenPublishing
import com.vanniktech.maven.publish.SonatypeHost

class MavenPublishPlugin : ConventionPlugin({
    with(pluginManager) {
        apply(libs.plugins.vanniktech.maven.publish.get().pluginId)
    }

    mavenPublishing {
        val version: String by properties
        coordinates(
            groupId = "io.morfly.compose",
            artifactId = project.name,
            version = version
        )

        pom {
            name.set("Advanced Bottom Sheet for Compose")
            description.set("Advanced bottom sheet component for Compose with flexible configuration")
            inceptionYear.set("2024")
            url.set("https://github.com/open-turo/nibel")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("Morfly")
                    name.set("Pavlo Stavytskyi")
                    url.set("https://github.com/Morfly")
                }
            }
            scm {
                url.set("https://github.com/Morfly/advanced-bottomsheet-compose")
                connection.set("scm:git:git://github.com/Morfly/advanced-bottomsheet-compose.git")
                developerConnection.set("scm:git:ssh://git@github.com/Morfly/advanced-bottomsheet-compose.git")
            }
        }

        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
})
