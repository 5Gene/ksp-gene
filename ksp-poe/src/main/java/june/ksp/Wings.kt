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

/**
 * 找到文件中所有注解，并收集注解参数
 */
fun KSClassDeclaration.readAnnotations(annotationFullName: String): List<Map<String, String>> {
    return annotations
        .filter { it.annotationType.asClassName() == annotationFullName }
        .map {
            it.arguments.associate { it.name!!.getShortName() to it.value!!.toString() }
        }.toList()

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