package gene.net.repository

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

/**
 * 方法添加参数,参数支持注解,注解的参数和方法参数名一致
 */
fun FunSpec.Builder.addAnnoParams(annoType: KClass<out Annotation>, params: List<String> = emptyList<String>()) {
    params.forEach {
        // 创建`id`参数
        val parameter = ParameterSpec.builder(it, String::class)
            .addAnnotation(AnnotationSpec.builder(annoType).addMember("\"$it\"", it).build())
            .build()
        addParameter(parameter)
    }
}

fun FunSpec.Builder.addAnnoParams(annoType: ClassName, params: List<String> = emptyList<String>()) {
    params.forEach {
        // 创建`id`参数
        val parameter = ParameterSpec.builder(it, String::class)
            .addAnnotation(AnnotationSpec.builder(annoType).addMember("\"$it\"", it).build())
            .build()
        addParameter(parameter)
    }
}

fun paramWithMap(paramName: String, keyClass: KClass<*>, valueClass: KClass<*>, isOverride: Boolean) = ParameterSpec.builder(
    paramName, Map::class.asTypeName().parameterizedBy(
        keyClass.asTypeName(),
        valueClass.asTypeName()
    )
).also { if (!isOverride) it.defaultValue("%M()", MemberName("kotlin.collections", "emptyMap")) }

fun paramWithList(paramName: String, valueClass: KClass<*>, isOverride: Boolean) = ParameterSpec.builder(
    paramName, List::class.asTypeName().parameterizedBy(
        valueClass.asTypeName()
    )
).also { if (!isOverride) it.defaultValue("%M()", MemberName("kotlin.collections", "emptyList")) }

fun paramWithList(paramName: String, ksClass: KSClassDeclaration, isOverride: Boolean) = ParameterSpec.builder(
    paramName, List::class.asTypeName().parameterizedBy(ksClass.toClassName())
).also { if (!isOverride) it.defaultValue("%M()", MemberName("kotlin.collections", "emptyList")) }

fun String.toClassName(): ClassName {
    val dotIndex = this.lastIndexOf(".")
    return ClassName(substring(0, dotIndex), substring(dotIndex + 1))
}


fun String.toMemberName(): MemberName {
    val dotIndex = this.lastIndexOf(".")
    return MemberName(substring(0, dotIndex), substring(dotIndex + 1))
}

fun topLevelFuncMember(packageName: String, funcName: String) = MemberName(packageName, funcName)

fun String.topLevelFuncMember(): MemberName {
    val dotIndex = this.lastIndexOf(".")
    return MemberName(substring(0, dotIndex), substring(dotIndex + 1))
}

/**
 * 样例：
 * ```
 * "java.lang.System.currentTimeMillis".invokeJavaStaticFunc()
 * ```
 * 生成：
 * ```
 * java.lang.System.currentTimeMillis()
 * ```
 */
context(CodeBlock.Builder)
fun String.invokeJavaStaticFunc(vararg params: String) {
    val dotIndex = this.lastIndexOf(".")
    val staticFunc = substring(dotIndex + 1)
    val javaClass = substring(0, dotIndex).toClassName()
    if (params.isEmpty()) {
        add("%T.$staticFunc()\n", javaClass)
    } else {
        val paramStr = params.joinToString(",")
        add("%T.$staticFunc($paramStr)\n", javaClass)
    }
}
//需要自动导包就要用ClassName()，占位符为%T
//顶级函数需要用MemberName()，占位符为%M

/**
 * 样例：
 * ```
 * "gene.net.repository.retrofitProvider".invokeTopLevelFunc()
 * ```
 * 生成：
 * ```
 * gene.net.repository.retrofitProvider()
 * ```
 */
context(CodeBlock.Builder)
fun String.invokeTopLevelFunc(vararg params: String) {
    val topFunc = this.toMemberName()
    if (params.isEmpty()) {
        add("%M()\n", topFunc)
    } else {
        val paramStr = params.joinToString(",")
        add("%M($paramStr)\n", topFunc)
    }
}

/**
 * 样例：
 * ```
 * "val ret = gene.net.repository.retrofitProvider().create"("com.example.DataPacksNetApi::class.java")
 *```
 * 生成：
 * ```
 * val ret = gene.net.repository.retrofitProvider().create"(com.example.DataPacksNetApi::class.java)
 * ```
 */
context(CodeBlock.Builder)
operator fun String.invoke(vararg params: String) {
    if (params.isEmpty()) {
        add("${this}()\n")
    } else {
        val paramStr = params.joinToString(",")
        add("${this}($paramStr)\n")
    }
}

/**
 * 样例：
 * ```
 * +"java.lang.System.currentTimeMillis()"
 * ```
 * 生成：
 * ```
 * java.lang.System.currentTimeMillis()
 * ```
 */
context(CodeBlock.Builder)
operator fun String.unaryPlus() {
    add("${this}\n")
}