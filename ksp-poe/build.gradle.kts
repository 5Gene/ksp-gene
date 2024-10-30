import june.wing.GroupIdMavenCentral
import june.wing.property
import june.wing.publishJavaMavenCentral
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(vcl.plugins.kotlin.jvm)
}

buildscript {
    dependencies {
        classpath(vcl.gene.conventions)
    }
}

kotlin {
    // Or shorter:
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
//        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
//        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

dependencies {
    api(vcl.ksp.process.api)
    api(libs.ksp.poe)
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    api(vcl.google.auto.service.anno)
}

group = GroupIdMavenCentral
version = wings.versions.gene.ksp.poe.get()

if (property("publish.ksp", "") == "poe") {
    publishJavaMavenCentral("ksp poe wings")
}