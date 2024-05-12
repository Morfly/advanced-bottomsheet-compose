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
            groupId = "io.morfly.TODO",
            artifactId = project.name,
            version = version
        )

        pom {
            name.set("TODO")
            description.set("TODO")
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
                url.set("https://github.com/open-turo/nibel")
                connection.set("scm:git:git://github.com/morfly/MultiState-BottomSheet.git")
                developerConnection.set("scm:git:ssh://git@github.com/morfly/MultiState-BottomSheet.git")
            }
        }

        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
})