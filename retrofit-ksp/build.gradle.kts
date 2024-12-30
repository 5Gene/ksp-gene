import june.wing.GroupIdMavenCentral
import june.wing.publishJavaMavenCentral
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(vcl.plugins.kotlin.jvm)
    alias(vcl.plugins.ksp)
}

buildscript {
    dependencies {
        classpath(vcl.gene.conventions)
    }
}

ksp {
    arg("ksp.logLevel", "info")
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    //每次都执行ksp
    outputs.upToDateWhen { false }
}

dependencies {
    ksp(vcl.gene.auto.service)
    implementation(wings.gene.ksp.poe)
//    implementation(project(":ksp-poe"))
    implementation(vcl.ksp.process.api)
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    implementation(vcl.google.auto.service.anno)
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

//https://kotlinlang.org/docs/ksp-incremental.html#aggregating-vs-isolating

group = GroupIdMavenCentral
version = wings.versions.gene.retrofit.ksp.asProvider().get()

publishJavaMavenCentral("ksp library for Retrofit,auto generate ServiceApi for data bean 🚀")


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
