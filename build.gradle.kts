buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.4.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
