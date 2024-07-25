package june.ksp.poe

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

/**
 * A<T>
 * 泛型 parameterizedBy
 */
fun ClassName.fanxing(T: ClassName) = this.parameterizedBy(T)

/**
 * 方法添加参数,参数支持注解,注解的参数和方法参数名一致
 */
fun FunSpec.Builder.addAnnoParams(annoType: KClass<out Annotation>, params: List<String> = emptyList<String>()) {
    params.forEach {
        // 创建`id`参数
        val parameter = ParameterSpec.builder(it, String::class)
            .addAnnotation(
                AnnotationSpec.builder(annoType)
                    .addMember("%S", it)
                    .build()
            )
            .build()
        addParameter(parameter)
    }
}

/**
 * 给方法添加参数，参数带注解
 * fun(@annoType("param") param:String)
 */
fun FunSpec.Builder.addAnnoParams(annoType: ClassName, params: List<String> = emptyList<String>()) {
    params.forEach {
        // 创建`id`参数
        val parameter = ParameterSpec.builder(it, String::class)
            .addAnnotation(
                AnnotationSpec.builder(annoType)
                    //%S，填充字符串 "$it"
                    .addMember("%S", it)
                    .build()
            )
            .build()
        addParameter(parameter)
    }
}

/**
 * 构建类型为Map的方法参数并带默认值
 * fun(paramName: Map<key:value>=emptyMap())
 */
fun paramWithMap(paramName: String, keyClass: KClass<*>, valueClass: KClass<*>, isOverride: Boolean) = paramWithMap(
    paramName,
    keyClass.asTypeName(),
    valueClass.asTypeName(),
    isOverride
)

/**
 * 构建类型为Map的方法参数并带默认值
 * fun(paramName: Map<keyClassName:valueClassName>=emptyMap())
 */
fun paramWithMap(paramName: String, keyClassName: TypeName, valueClassName: TypeName, isOverride: Boolean) = ParameterSpec.builder(
    paramName,
    Map::class.asTypeName().parameterizedBy(
        keyClassName,
        valueClassName
    )
).also { if (!isOverride) it.defaultValue("%M()", MemberName("kotlin.collections", "emptyMap")) }

/**
 * 构建类型为`Map`的方法参数并带默认值
 * fun(paramName: Map<String:Any>=emptyMap())
 */
fun paramWithMap(paramName: String, isOverride: Boolean) = paramWithMap(
    paramName,
    String::class.asTypeName(),
    Any::class.asTypeName().copy(
        //不加这个注解retrofit调用会报错
        annotations = listOf(AnnotationSpec.builder(JvmSuppressWildcards::class).build())
    ),
    isOverride
)

/**
 * 构建类型为`List`的方法参数并带默认值
 * fun(paramName: List<valueClass>=emptyList())
 */
fun paramWithList(paramName: String, valueClass: KClass<*>, isOverride: Boolean) = paramWithList(paramName, valueClass.asTypeName(), isOverride)

/**
 * 构建类型为`List`的方法参数并带默认值
 * fun(paramName: List<ksClassDeclaration>=emptyList())
 */
fun paramWithList(paramName: String, ksClassDeclaration: KSClassDeclaration, isOverride: Boolean) =
    paramWithList(paramName, ksClassDeclaration.toClassName(), isOverride)

/**
 * 构建类型为`List`的方法参数并带默认值
 * fun(paramName: List<valueClassName>=emptyList())
 */
fun paramWithList(paramName: String, valueClassName: TypeName, isOverride: Boolean) = ParameterSpec.builder(
    paramName, List::class.asTypeName().parameterizedBy(valueClassName)
).also { if (!isOverride) it.defaultValue("%M()", MemberName("kotlin.collections", "emptyList")) }


fun String.toClassName(): ClassName {
    val dotIndex = this.lastIndexOf(".")
    return ClassName(substring(0, dotIndex), substring(dotIndex + 1))
}

/**
 * 顶级函数
 * 顶级成员变量
 */
fun String.toMemberName(): MemberName {
    val dotIndex = this.lastIndexOf(".")
    return MemberName(substring(0, dotIndex), substring(dotIndex + 1))
}

/**
 * 顶级函数
 */
fun topLevelFuncMember(packageName: String, funcName: String) = MemberName(packageName, funcName)

/**
 * 顶级函数
 * "kotlin.collections.emptyList".topLevelFuncMember()
 */
fun String.topLevelFunc(): MemberName {
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