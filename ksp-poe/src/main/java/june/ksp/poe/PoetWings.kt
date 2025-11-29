package june.ksp.poe

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.CodeBlock.Builder
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

/**
 * A<T>
 * 泛型 parameterizedBy
 */
fun ClassName.fanxing(vararg typeArguments: TypeName) = parameterizedBy(*typeArguments)

fun listWithType(typeName: TypeName): TypeName {
    return LIST.fanxing(typeName)
}

fun mapWithType(keyClassName: TypeName, valueClassName: TypeName): TypeName {
    return MAP.fanxing(keyClassName, valueClassName)
}

//Local variables
fun CodeBlockBuilder.addLocalMapVariable(refName: String, params: List<String>) {
    builder.apply {
        mapOfBuilder(refName, params)
    }
}

fun CodeBlock.Builder.mapOfBuilder(refName: String, params: List<String>) {
    val mapOf = MemberName("kotlin.collections", "mapOf")
    val to = MemberName("kotlin", "to")
    add("val $refName = %M(\n", mapOf)
    params.forEach { k ->
        add("   %S %M $k,\n", k, to)
    }
    add(")\n")
}

fun CodeBlockBuilder.listOfBuilder(refName: String, params: List<String>) {
    builder.apply {
        listOfBuilder(refName, params)
    }
}

fun CodeBlock.Builder.listOfBuilder(refName: String, values: List<String>) {
    listOf<String>("a", "b", "c")
    MemberName("kotlin.collections", "listOf")
    val listOf = MemberName("kotlin.collections", "listOf")
    add("val $refName = %M(%s)\n", listOf, values.joinToString(","))
}


fun String.toClassName(): ClassName {
    try {
        return ClassName.bestGuess(this)
    } catch (e: Exception) {
        val dotIndex = this.lastIndexOf(".")
        return ClassName(substring(0, dotIndex), substring(dotIndex + 1))
    }
}

/**
 * 顶级函数
 * 顶级成员变量
 */
fun String.toMemberName(): MemberName {
    val dotIndex = this.lastIndexOf(".")
    return MemberName(substring(0, dotIndex), substring(dotIndex + 1))
}

fun String.toKtTopLevel(): MemberName {
    return toMemberName()
}

/**
 * 顶级函数
 */
fun topLevelFuncMember(packageName: String, funcName: String) = MemberName(packageName, funcName)

/**
 * 顶级函数
 * "kotlin.collections.emptyList".topLevelFunc()
 */
fun String.topLevelFunc(): MemberName {
    val dotIndex = this.lastIndexOf(".")
    return MemberName(substring(0, dotIndex), substring(dotIndex + 1))
}

//需要自动导包就要用ClassName()，占位符为%T
//顶级函数需要用MemberName()，占位符为%M


public inline fun buildCodeBlock(builderAction: CodeBlockBuilder.() -> Unit): CodeBlock {
    return CodeBlockBuilder(CodeBlock.builder()).apply(builderAction).build()
}

class CodeBlockBuilder(val builder: Builder) {
    fun build(): CodeBlock {
        return builder.build()
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
    fun String.invokeJavaStaticFunc(vararg params: String) {
        val dotIndex = this.lastIndexOf(".")
        val staticFunc = substring(dotIndex + 1)
        val javaClass = substring(0, dotIndex).toClassName()
        if (params.isEmpty()) {
            builder.add("%T.$staticFunc()\n", javaClass)
        } else {
            val paramStr = params.joinToString(",")
            builder.add("%T.$staticFunc($paramStr)\n", javaClass)
        }
    }

    /**
     * 样例：
     * ```
     * "gene.retrofit.repository.retrofitProvider".invokeTopLevelFunc()
     * ```
     * 生成：
     * ```
     * gene.retrofit.repository.retrofitProvider()
     * ```
     */
    fun String.invokeTopLevelFunc(vararg params: String) {
        val topFunc = this.toMemberName()
        if (params.isEmpty()) {
            builder.add("%M()\n", topFunc)
        } else {
            val paramStr = params.joinToString(",")
            builder.add("%M($paramStr)\n", topFunc)
        }
    }

    /**
     * 样例：
     * ```
     * "val ret = gene.retrofit.repository.retrofitProvider().create"("com.example.DataPacksNetApi::class.java")
     *```
     * 生成：
     * ```
     * val ret = gene.retrofit.repository.retrofitProvider().create(com.example.DataPacksNetApi::class.java)
     * ```
     */
    operator fun String.invoke(vararg params: String) {
        if (params.isEmpty()) {
            builder.add("${this}()\n")
        } else {
            val paramStr = params.joinToString(",")
            builder.add("${this}($paramStr)\n")
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
    operator fun String.unaryPlus() {
        builder.add("${this}\n")
    }


    /**
     * kotlin顶级函数用
     */
    fun String.M(vararg memberName: MemberName) {
        builder.add("$this\n", *memberName)
    }

    /**
     * 完整类名用
     */
    fun String.T(vararg className: ClassName) {
        builder.add("$this\n", *className)
    }

    /**
     * 格式化
     */
    fun String.codeFormat(vararg args: Any) {
        builder.add("$this\n", *args)
    }

    fun String.cf(vararg args: Any) {
        builder.add("$this\n", *args)
    }
}
