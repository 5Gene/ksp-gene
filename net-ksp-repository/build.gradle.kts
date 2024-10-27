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

dependencies {
    implementation(wings.gene.ksp.poe)
    implementation(vcl.gene.auto.service)
    implementation(vcl.ksp.process.api)
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    implementation(vcl.google.auto.service.anno)
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

//https://kotlinlang.org/docs/ksp-incremental.html#aggregating-vs-isolating

group = GroupIdMavenCentral
version = wings.versions.gene.net.ksp.repository.get()

if (property("publish.ksp", "") == "repository") {
    publishJavaMavenCentral("ksp library for Retrofit,auto generate ServiceApi for data bean 🚀")
}


//KSFile
//  packageName: KSName
//  fileName: String
//  annotations: List<KSAnnotation>  (File annotations)
//  declarations: List<KSDeclaration>
//    KSClassDeclaration // class, interface, object
//      simpleName: KSName
//      qualifiedName: KSName
//      containingFile: String
//      typeParameters: KSTypeParameter
//      parentDeclaration: KSDeclaration
//      classKind: ClassKind
//      primaryConstructor: KSFunctionDeclaration
//      superTypes: List<KSTypeReference>
//      // contains inner classes, member functions, properties, etc.
//      declarations: List<KSDeclaration>
//    KSFunctionDeclaration // top level function
//      simpleName: KSName
//      qualifiedName: KSName
//      containingFile: String
//      typeParameters: KSTypeParameter
//      parentDeclaration: KSDeclaration
//      functionKind: FunctionKind
//      extensionReceiver: KSTypeReference?
//      returnType: KSTypeReference
//      parameters: List<KSValueParameter>
//      // contains local classes, local functions, local variables, etc.
//      declarations: List<KSDeclaration>
//    KSPropertyDeclaration // global variable
//      simpleName: KSName
//      qualifiedName: KSName
//      containingFile: String
//      typeParameters: KSTypeParameter
//      parentDeclaration: KSDeclaration
//      extensionReceiver: KSTypeReference?
//      type: KSTypeReference
//      getter: KSPropertyGetter
//        returnType: KSTypeReference
//      setter: KSPropertySetter
//        parameter: KSValueParameter
