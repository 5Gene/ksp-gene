package gene.retrofit.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import june.ksp.poe.CodeBlockBuilder
import june.ksp.poe.addAnnoParams
import june.ksp.poe.addStringParams
import june.ksp.poe.buildCodeBlock
import june.ksp.poe.fanxing
import june.ksp.poe.listWithType
import june.ksp.poe.mapOfBuilder
import june.ksp.poe.paramWithMap


/**
 * interface定义的接口函数名+参数+返回值
 * impl定义的接口函数名+参数+返回值
 */
fun restApiFun(
    netSourceAnno: Map<String, String>,
    //返回值类型
    resultKsClass: KSClassDeclaration,
    //NetResult<T>类型, interface中必传，实现类中，可选
    netResultClass: ClassName?,
    params: String = "",
): Pair<FunSpec.Builder, String> {
    val path by netSourceAnno
    val list by netSourceAnno
    val isListResult = list.toBoolean()

    val paths = findPath.findAll(path).map { it.groupValues[1] }.toList()
    val funName = path.substringAfterLast("/")

    val actualParams = mutableListOf<String>()

    val funBuilder = FunSpec.builder(funName).addModifiers(KModifier.SUSPEND).apply {
        //<editor-fold desc="添加方法参数">
        if (paths.isNotEmpty()) {
            //添加path参数
            addAnnoParams(Path, paths)
            actualParams.addAll(paths)
        }
        if (params.isEmpty()) {
            //没有定义接口请求参数，那么使用Map参数
            val queryMap = paramWithMap("params", true)
                .addAnnotation(QueryMap)
                .build()
            addParameter(queryMap)
            actualParams.add("params")
        } else {
            //添加定义的接口请求参数
            val split = params.split(",")
            addStringParams(split)
        }
        //</editor-fold>

        //<editor-fold desc="添加方法返回值">
        val resultClass = resultKsClass.toClassName()
        if (isListResult) {
            val listResult = listWithType(resultClass)
            returns(
                netResultClass?.fanxing(listResult) ?: listResult
            )
        } else {
            returns(netResultClass?.fanxing(resultClass) ?: resultClass)
        }
        //</editor-fold>
    }

    return funBuilder to actualParams.joinToString(",")
}


fun TypeSpec.Builder.addRestApiFunction(
    netSourceAnno: Map<String, String>,
    //返回值类型
    resultKsClass: KSClassDeclaration,
    //NetResult<T>类型, interface中必传，实现类中，可选
    netResultClass: ClassName,
) {
    val method by netSourceAnno
    val path by netSourceAnno
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

    addFunction(
        restApiFun(netSourceAnno, resultKsClass, netResultClass).first
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(annotationSpec).build()
    )
}

fun TypeSpec.Builder.addRestApiImplFunction(
    netSourceAnno: Map<String, String>,
    //返回值类型
    resultKsClass: KSClassDeclaration,
    //NetResult<T>类型, interface中必传，实现类中，可选
    netResultClass: ClassName?,
    retrofit: MemberName,
    netApiClassName: String,
    exception: ClassName
) {
    val extra by netSourceAnno
    val params by netSourceAnno
    val check by netSourceAnno
    val needCheckResult = check.toBoolean()

    val path by netSourceAnno
    val funName = path.substringAfterLast("/")
    fun CodeBlockBuilder.returnBody() {
        """
if (!netResult.isOk()) {
    throw %T(netResult.code(), netResult.message())
}
return netResult.body()
        """.trim().T(exception)
    }

    val (funcBuilder, wantParams) = restApiFun(
        netSourceAnno, resultKsClass,
        if (needCheckResult) null else netResultClass,
        params
    )

    funcBuilder.addCode(buildCodeBlock {
        val actualParams = if (wantParams.endsWith("params")) {
            //没定义接口具体参数
            wantParams
        } else {
            mapOfBuilder("params", params.split(","))
            "$wantParams,params"
        }

        if (extra.isEmpty()) {
//                            add("return retrofit.%N(${actualParams})\n",funName)
            if (needCheckResult) {
                +"val netResult = retrofit.$funName(${actualParams})"
                returnBody()
            } else {
                +"return retrofit.$funName(${actualParams})"
            }
        } else {
            if (needCheckResult) {
                "val netResult = %M(%S).create(%N::class.java).%N(${actualParams})".cf(
                    retrofit,
                    extra,
                    netApiClassName,
                    funName
                )
                returnBody()
            } else {
                "return %M(%S).create(%N::class.java).%N(${actualParams})".codeFormat(
                    retrofit,
                    extra,
                    netApiClassName,
                    funName
                )
            }
        }
    })
    addFunction(funcBuilder.build())
}