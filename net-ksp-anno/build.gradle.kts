import june.wing.GroupIdMavenCentral
import june.wing.property
import june.wing.publishJavaMavenCentral

plugins {
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

group = GroupIdMavenCentral
version = wings.versions.gene.net.ksp.anno.get()

if (property("publish.ksp", "") == "repository") {
    publishJavaMavenCentral("annotation for NetSource")
}