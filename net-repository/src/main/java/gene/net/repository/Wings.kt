package gene.net.repository

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType


//<editor-fold desc="extensions for kt">
val String.yellow: String
    get() = "\u001B[93m${this}\u001B[0m"

fun KSType.fullClassName() = declaration.fullClassName()

fun KSDeclaration.fullClassName() = qualifiedName!!.asString()

fun KSDeclaration.packageName() = packageName.asString()

fun KSDeclaration.simpleName() = simpleName.asString()

val String.lookDown: String
    get() = "👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇 $this 👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇"

val String.lookup: String
    get() = "👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆 $this 👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆"

fun KSClassDeclaration.readAnnotations(): Map<String, String>? {
    return annotations.find { it.annotationType.resolve().fullClassName() == NET_SOURCE_ANNO }
        ?.arguments?.map { it.name!!.getShortName() to it.value!!.toString() }?.toMap()
}

fun KSClassDeclaration.fileName() = containingFile!!.fileName.removeSuffix(".kt")

//org.gradle.logging.level=info
fun String.logInfo(logger: KSPLogger) {
    logger.info(this)
}

fun String.logWarn(logger: KSPLogger) {
    logger.warn(this)
}
//</editor-fold>