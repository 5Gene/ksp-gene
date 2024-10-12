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


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    api("com.squareup.retrofit2:retrofit:2.5.0")
}

group = "io.github.5gene"
version = wings.versions.gene.net.ksp.anno.get()

if (property("publish.ksp", "") == "repository") {
    publishJavaMavenCentral("annotation for NetSource")
}