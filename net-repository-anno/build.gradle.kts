import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import wing.property
import wing.publishJavaMavenCentral

plugins {
    id("java-library")
    alias(vcl.plugins.kotlin.jvm)
}

buildscript {
    dependencies {
        classpath(vcl.gene.conventions)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    implementation(kotlin("stdlib-jdk8"))
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    api("com.squareup.retrofit2:retrofit:2.5.0")
}

group = "io.github.5gene"
version = "0.0.4"

if (property("publish.ksp", "") == "repository") {
    publishJavaMavenCentral("annotation for NetSource")
}