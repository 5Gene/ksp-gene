package june.ksp.poe

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

/**
 * %L	Literal（字面量，不加引号）	%L → 42, true, null, MyClass.INSTANCE
 * %S	String（自动加双引号并转义）	%S → "hello"
 * %T	Type（类型，自动处理 import）	%T → java.util.ArrayList（会 import）
 * %N	Name（标识符，防关键字）	%N → `class`（如果名字是关键字）
 * %M	Member（函数/属性引用）	%M → kotlin.collections.emptyList
 */
fun FunSpec.Builder.parameter(
    name: String,
    type: TypeName,
    more: (ParameterSpec.Builder.() -> Unit)? = null
): FunSpec.Builder {
    val parameterBuilder = ParameterSpec
        .builder(name, type)
    if (more != null) {
        parameterBuilder.more()
    }
    return addParameter(parameterBuilder.build())
}


/**
 * 给方法添加参数，参数带注解
 * fun(@annoType("param") param:String)
 */
fun FunSpec.Builder.annoParameters(
    annoType: ClassName,
    parameterType: ClassName,
    params: List<String>
): FunSpec.Builder {
    params.forEach {
        parameter(it, parameterType) {
            annotation(annoType) {
                //%S，填充字符串 "$it"
                addMember("%S", it)
            }
        }
    }
    return this
}


public inline fun FunSpec.Builder.codeBlock(builderAction: CodeBlockBuilder.() -> Unit) {
    addCode(buildCodeBlock(builderAction))
}


fun FunSpec.Builder.stringParams(params: List<String> = emptyList<String>()) {
    params.forEach {
        addParameter(
            ParameterSpec
                .builder(it, STRING)
                .defaultValue("%S", "")
                .build()
        )
    }
}

fun FunSpec.Builder.anyParams(
    params: List<String> = emptyList<String>(),
    annotation: ClassName? = null,
    more: (ParameterSpec.Builder.() -> Unit)? = null
) {
    params.forEach {
        parameter(it, ANY) {
            defaultValue("%S", "")
            annotation?.let {
                addAnnotation(it)
            }
            more?.invoke(this)
        }
    }
}


//@JvmSuppressWildcards 是 Kotlin 提供的一个 面向 JVM 的注解，用于控制 Kotlin 编译器在生成 Java 字节码时 是否保留泛型中的通配符（wildcards）。
//它的主要作用是：抑制（suppress）Kotlin 编译器自动插入的 ? extends T 或 ? super T 通配符，从而生成更“干净”或与 Java 期望兼容的泛型签名。

fun FunSpec.Builder.mapParamsWithJvmSuppressWildcards(
    name: String,
    keyClassName: TypeName = STRING,
    valueClassName: TypeName = ANY,
    annotation: ClassName? = null,
): FunSpec.Builder {
    return mapParams(
        name, keyClassName, valueClassName.copy(
            //不加这个注解retrofit调用会报错
            annotations = listOf(AnnotationSpec.builder(JvmSuppressWildcards::class).build())
        ), annotation
    )
}

fun FunSpec.Builder.mapParams(
    name: String,
    keyClassName: TypeName = STRING,
    valueClassName: TypeName = ANY,
    annotation: ClassName? = null,
): FunSpec.Builder {

    return parameter(name, mapWithType(keyClassName, valueClassName)) {
        if (annotation != null) {
            addAnnotation(annotation)
        }
        defaultEmptyMap()
    }
}

fun FunSpec.Builder.listParamsWithJvmSuppressWildcards(
    name: String,
    typeClassName: TypeName = ANY,
    annotation: ClassName? = null,
): FunSpec.Builder {
    return listParams(
        name, typeClassName.copy(
            //不加这个注解retrofit调用会报错
            annotations = listOf(AnnotationSpec.builder(JvmSuppressWildcards::class).build())
        ), annotation
    )
}

fun FunSpec.Builder.listParams(
    name: String,
    typeClassName: TypeName = ANY,
    annotation: ClassName? = null,
): FunSpec.Builder {
    return parameter(name, listWithType(typeClassName)) {
        if (annotation != null) {
            addAnnotation(annotation)
        }
        defaultEmptyList()
    }
}