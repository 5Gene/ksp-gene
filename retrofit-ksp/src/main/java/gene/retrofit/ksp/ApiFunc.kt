package gene.retrofit.ksp

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.ksp.toClassName
import june.ksp.poe.CodeBlockBuilder
import june.ksp.poe.addLocalMapVariable
import june.ksp.poe.annoParameters
import june.ksp.poe.anyParams
import june.ksp.poe.codeBlock
import june.ksp.poe.defaultString
import june.ksp.poe.fanxing
import june.ksp.poe.listWithType
import june.ksp.poe.mapParams
import june.ksp.poe.mapParamsWithJvmSuppressWildcards

class ApiFunc(
    val environment: SymbolProcessorEnvironment,
    annotation: Map<String, String>,
    val dtoClass: KSClassDeclaration,
    val netResultClass: ClassName,
) {
    val method by annotation
    val path by annotation
    val params: String by annotation
    val extra by annotation
    val isCheckResult = annotation["check"].toBoolean()
    val isListResult = annotation["list"].toBoolean()
    val isResultNullable = annotation["nullable"].toBoolean()
    val paths = findPath.findAll(path).map { it.groupValues[1] }.toList()
    val funcName = path.substringAfterLast("/")


    fun buildApiAbstractFunc(): FunSpec {
        val annotationSpec = if (method.equals("get", true)) {
            AnnotationSpec.builder(GET).addMember("\"$path\"").build()
        } else if (method.equals("post", true)) {
            AnnotationSpec.builder(POST).addMember("\"$path\"").build()
        } else if (method.equals("put", true)) {
            AnnotationSpec.builder(PUT).addMember("\"$path\"").build()
        } else if (method.equals("delete", true)) {
            AnnotationSpec.builder(DELETE).addMember("\"$path\"").build()
        } else {
            throw RuntimeException("not support $method")
        }

        //api接口类中，参数固定成 @QueryMap params: Map<String, Any>
        //在调用类中根据注解定义的参数构建成Map传入
        //api接口类的方法和调用类的方法参数唯一的区别就是接口参数的形式
        return FunSpec.builder(funcName)
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(annotationSpec).apply {
                //<editor-fold desc="定义形参">
                if (paths.isNotEmpty()) {
                    //添加path参数
                    annoParameters(Path, STRING, paths)
                }
                //@Query / @QueryMap：用于 URL 查询参数（GET 请求常见）
                //@Body：用于将整个对象作为请求体（常用于 POST/PUT）
                val paramAnno = if (method.equals("POST", true) || method.equals("PUT", true)) {
                    Body
                } else {
                    QueryMap
                }
                if (params.isEmpty() && !method.equals("get", true)) {
                    addParameter(
                        ParameterSpec
                            .builder("params", ANY)
                            .defaultString("")
                            .addAnnotation(paramAnno)
                            .build()
                    )
                } else {
                    mapParamsWithJvmSuppressWildcards("params", annotation = paramAnno)
                }
                //</editor-fold>

                //<editor-fold desc="定义返回值">
                val resultClass = dtoClass.toClassName().copy(nullable = isResultNullable)
                if (isListResult) {
                    val listResult = listWithType(resultClass)
                    returns(netResultClass.fanxing(listResult))
                } else {
                    returns(netResultClass.fanxing(resultClass))
                }
                //</editor-fold>
            }.build()
    }

    fun buildApiImplFunc(
        retrofit: MemberName,
        netApiClassName: String,
        exception: ClassName
    ): FunSpec {
        return FunSpec.builder(funcName)
            .addModifiers(KModifier.SUSPEND).apply {
                val paramList = if (params.isEmpty()) listOf("params") else params.split(",")
                val inParams = StringBuilder()
                //<editor-fold desc="定义形参">
                if (paths.isNotEmpty()) {
                    paths.forEach {
                        inParams.append(it).append(",")
                        addParameter(
                            ParameterSpec.builder(it, STRING).build()
                        )
                    }
                }
                inParams.append("params")
                if (method.equals("get", true) && params.isEmpty()) {
                    //get方法如果没参数必须使用map
                    mapParams("params")
                } else {
                    anyParams(paramList)
                }
                //</editor-fold>

                //<editor-fold desc="定义返回值">
                val resultClass = dtoClass.toClassName().copy(nullable = isResultNullable)
                if (isListResult) {
                    val listResult = listWithType(resultClass)
                    if (isCheckResult) {
                        returns(listResult)
                    } else {
                        returns(netResultClass.fanxing(listResult))
                    }
                } else {
                    if (isCheckResult) {
                        returns(resultClass)
                    } else {
                        returns(netResultClass.fanxing(resultClass))
                    }
                }
                //</editor-fold>

                //<editor-fold desc="定义方法体">
                codeBlock {
                    //wantParams 包括path,和接口参数 params列表
                    val actualParams = inParams.toString()
                    if (params.isNotEmpty()) {
                        addLocalMapVariable("params", paramList)
                    }

                    fun CodeBlockBuilder.returnBodyWithCheckResult() {
                        """
if (!netResult.isOk()) {
    throw %T(netResult.code(), netResult.message())
}
return netResult.body()
        """.trim().T(exception)
                    }

                    if (extra.isEmpty()) {
                        if (isCheckResult) {
                            +"val netResult = retrofit.$funcName(${actualParams})"
                            returnBodyWithCheckResult()
                        } else {
                            +"return retrofit.$funcName(${actualParams})"
                        }
                    } else {
                        if (isCheckResult) {
                            "val netResult = %M(%S).create(%N::class.java).%N(${actualParams})".cf(
                                retrofit,
                                extra,
                                netApiClassName,
                                funcName
                            )
                            returnBodyWithCheckResult()
                        } else {
                            "return %M(%S).create(%N::class.java).%N(${actualParams})".codeFormat(
                                retrofit,
                                extra,
                                netApiClassName,
                                funcName
                            )
                        }
                    }
                }
                //</editor-fold>
            }.build()
    }
}