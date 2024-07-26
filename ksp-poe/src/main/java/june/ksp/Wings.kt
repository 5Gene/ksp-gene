package june.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference


//<editor-fold desc="extensions for kt">
val String.yellow: String
    get() = "\u001B[93m${this}\u001B[0m"

fun KSType.asClassName() = declaration.asClassName()

fun KSDeclaration.asClassName() = qualifiedName!!.asString()

fun KSTypeReference.asClassName() = resolve().asClassName()

fun KSDeclaration.asPackageName() = packageName.asString()

fun KSDeclaration.asSimpleName() = simpleName.asString()

fun KSClassDeclaration.readAnnotations(annotationFullName: String): Map<String, String>? {
    return annotations
        .find { it.annotationType.asClassName() == annotationFullName }
        ?.arguments?.associate { it.name!!.getShortName() to it.value!!.toString() }
}

val String.lookDown: String
    get() = "👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇 $this 👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇"

val String.lookup: String
    get() = "👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆 $this 👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆"

fun KSClassDeclaration.fileName() = containingFile!!.fileName.removeSuffix(".kt")

//org.gradle.logging.level=info
fun String.logInfo(logger: KSPLogger) {
    logger.info(this)
}

fun String.logWarn(logger: KSPLogger) {
    logger.warn(this)
}
//</editor-fold>