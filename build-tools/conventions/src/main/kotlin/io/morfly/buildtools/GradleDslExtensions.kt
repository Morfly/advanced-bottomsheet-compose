package io.morfly.buildtools

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

// Makes 'libs' version catalog available for precompiled plugins in a type-safe manner.
// https://github.com/gradle/gradle/issues/15383#issuecomment-1245546796
val Project.libs get() = extensions.getByType<LibrariesForLibs>()

fun Project.mavenPublishing(configure: MavenPublishBaseExtension.() -> Unit) {
    extensions.configure("mavenPublishing", configure)
}