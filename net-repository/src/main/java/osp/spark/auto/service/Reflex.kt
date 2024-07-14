package osp.spark.auto.service

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import java.io.File
import java.lang.reflect.Method


fun Any.getDeclaredMethod2(name: String, vararg parameterTypes: Class<*>): Method {
    val method = this.javaClass.getDeclaredMethod(name, *parameterTypes)
    method.isAccessible = true
    return method
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.getDeclaredField2(name: String): T {
    val field = this::class.java.getDeclaredField(name)
    field.isAccessible = true
    return field.get(this) as T
}


// 以下反射来自源码
// https://github1s.com/google/ksp/blob/main/common-util/src/main/kotlin/com/google/devtools/ksp/common/impl/CodeGeneratorImpl.kt

fun CodeGenerator.projectBase(): File {
    return getDeclaredField2("projectBase")
}

fun CodeGenerator.resourcesDir(): File {
    return getDeclaredField2("resourcesDir")
}

fun CodeGenerator.anyChangesWildcard(): KSFile {
    return getDeclaredField2("anyChangesWildcard")
}

fun CodeGenerator.fileMap(): MutableMap<String, File> {
    return getDeclaredField2("fileMap")
}

fun CodeGenerator.pathOf(packageName: String, fileName: String, extension: String): String {
    //    fun pathOf(packageName: String, fileName: String, extensionName: String): String {
    return getDeclaredMethod2("pathOf", String::class.java, String::class.java, String::class.java)
        .invoke(this, packageName, fileName, extension) as String
}

//fun CodeGenerator.resourcesDirCache(): File {
//    val resourcesDireCache = getDeclaredField2<File>("resourcesDir").absolutePath.replace("generated", "kspCaches")
//    return File(resourcesDireCache).apply {
//        if (!parentFile.exists()) {
//            parentFile.mkdirs()
//        }
//    }
//}
fun CodeGenerator.extensionToDirectoryCache(extension: String): File {
    // baseDir = extensionToDirectory(extensionName: String)
    val extensionToDirectory = getDeclaredMethod2("extensionToDirectory", String::class.java)
        .invoke(this, extension) as File
    val extensionToDirectoryCache = extensionToDirectory.absolutePath.replace("generated", "kspCaches")
    return File(extensionToDirectoryCache)
}

fun CodeGenerator.extensionToDirectory(extension: String): File {
    // baseDir = extensionToDirectory(extensionName: String)
    val extensionToDirectory = getDeclaredMethod2("extensionToDirectory", String::class.java)
        .invoke(this, extension) as File
    val extensionToDirectoryCache = extensionToDirectory.absolutePath.replace("generated", "kspCaches")
    return File(extensionToDirectoryCache)
}


fun SymbolProcessorEnvironment.getGeneratedFiles(): Collection<File> {
    if (codeGenerator.generatedFile.isEmpty()) {
        "$ $this ➱ environment.codeGenerator.generatedFile > isEmpty !! ".logInfo(logger)
        val fileMap = codeGenerator.fileMap()
        return fileMap.values
    }
    return codeGenerator.generatedFile
}

/**
 * 不能直接往生成的文件里面写内容，这样就无法关联文件了
 *
 */
fun SymbolProcessorEnvironment.getGeneratedFileCacheByNameAndExtension(packageName: String, fileName: String, extension: String): File {
    val baseDir: File = codeGenerator.extensionToDirectoryCache(extension)
    val path = codeGenerator.pathOf(packageName, fileName, extension)
    return File(baseDir, path).apply {
        if (!exists()) {
            parentFile.mkdirs()
            createNewFile()
        }
    }
}

fun SymbolProcessorEnvironment.getGeneratedPathByNameAndExtension(packageName: String, fileName: String, extension: String): String {
    val baseDir: File = codeGenerator.extensionToDirectory(extension)
    return codeGenerator.pathOf(packageName, fileName, extension)
}