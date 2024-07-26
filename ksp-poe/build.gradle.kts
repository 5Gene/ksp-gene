import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
}

buildscript {
    dependencies {
        classpath(wings.conventions)
    }
}

kotlin {
    // Or shorter:
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xcontext-receivers")
//        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
//        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

dependencies {
    api(libs.ksp.process.api)
    api(libs.ksp.poe)
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    api(libs.google.auto.service.anno)
}

group = "io.github.5gene"
version = wings.versions.ksp.poe.get()

//publishJavaMavenCentral("ksp poe wings")