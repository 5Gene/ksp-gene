package gene.net.repository

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
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

fun topLevelFuncMember(packageName: String, funcName: String) = MemberName(packageName, funcName)

fun String.topLevelFuncMember(): MemberName {
    val dotIndex = this.lastIndexOf(".")
    return MemberName(substring(0, dotIndex), substring(dotIndex + 1))
}