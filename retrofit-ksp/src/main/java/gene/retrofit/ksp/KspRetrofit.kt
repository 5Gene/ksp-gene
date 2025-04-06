package gene.retrofit.ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import june.ksp.asPackageName
import june.ksp.fileName
import june.ksp.poe.buildCodeBlock
import june.ksp.poe.toClassName
import june.ksp.poe.topLevelFunc
import june.ksp.readAnnotations

const val NET_SOURCE_ANNO = "gene.retrofit.anno.NetSource"

@AutoService(SymbolProcessorProvider::class)
class KspRetrofitSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KspRetrofitSymbolProcessor(environment)
    }
}

/**
 * 文件名, 文件, 类 ,多个注解
 */
data class NetDataStruct(
    val fileName: String,
    val ksFile: KSFile,
    val packageName: String,
    val ksClass: KSClassDeclaration,
    val netSourceAnnos: List<Map<String, String>>
)

val Path = "retrofit2.http.Path".toClassName()
val QueryMap = "retrofit2.http.QueryMap".toClassName()
val GET = "retrofit2.http.GET".toClassName()
val POST = "retrofit2.http.POST".toClassName()
val PUT = "retrofit2.http.PUT".toClassName()
val DELETE = "retrofit2.http.DELETE".toClassName()

val findPath = """\{(.*?)\}""".toRegex()
val Exception = "gene.retrofit.anno.NetException".toClassName()

class KspRetrofitSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    //    private val NetResulcClass: ClassName by lazy { ClassName.bestGuess(environment.options["NetResult"]!!) }
    private val netResultClass: ClassName by lazy { environment.options["NetResult"]!!.toClassName() }
    private val exceptionClass: ClassName by lazy { environment.options["Exception"]?.toClassName() ?: Exception }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!environment.options.containsKey("NetResult")) {
            environment.logger.error(
                """
                you have to set "NetResult" by option as class name for read net response data
                and the "NetResult" class must be subtype of gene.retrofit.anno.INetResult
                for example:
                
                ksp {
                    arg("NetResult", "your.net.data.wrapper.class.name")
                }
                
            """.trimIndent()
            )
        }
        val symbolsWithAnnotation = resolver.getSymbolsWithAnnotation(NET_SOURCE_ANNO)
        if (symbolsWithAnnotation.toList().isEmpty()) {
            return emptyList()
        }
        symbolsWithAnnotation.filter { it.validate() }
            //找到所有注解的文件
            .filterIsInstance<KSClassDeclaration>()
            .map {
                //找到文件中的所有注解，并收集注解参数
                NetDataStruct(
                    it.fileName(),
                    it.containingFile!!, it.asPackageName(),
                    it,
                    it.readAnnotations(NET_SOURCE_ANNO)
                )
            }.groupBy { it.fileName }
            .map { it.value }
            .forEach(::generateNetService)
        return emptyList()
    }

    private fun generateNetService(dataStructs: List<NetDataStruct>) {
        val dataStruct = dataStructs.first()
        val netApiClassName = "${dataStruct.fileName}NetApi"
        environment.logger.warn("generateNetService --> $netApiClassName")

        val interfaceBuilder = TypeSpec.interfaceBuilder(netApiClassName).addModifiers(KModifier.PRIVATE)
        val retrofit = "gene.retrofit.anno.retrofitProvider".topLevelFunc()
        val objectBuilder = TypeSpec
            .objectBuilder("${dataStruct.fileName}NetSource")
            .addProperty(
                PropertySpec.builder(
                    "retrofit",
                    ClassName(dataStruct.packageName, netApiClassName),
                    KModifier.PRIVATE
                ).delegate(
                    buildCodeBlock {
                        //delegate会自己带by关键字
                        "lazy { %M(%S).create(%N::class.java) \n}".codeFormat(retrofit, "", netApiClassName)
                    }
                ).build()
//                    .initializer(buildCodeBlock {
//                        "%M(%S).create(%N::class.java)".codeFormat(retrofit, "", netApiClassName)
//                    })
            )

        dataStructs.forEach {
            environment.logger.warn("generateNetService --> dataStructs:${it.netSourceAnnos}")
            it.netSourceAnnos.forEach { netSourceAnno ->
                interfaceBuilder.addRestApiFunction(
                    netSourceAnno, it.ksClass, netResultClass
                )
                objectBuilder.addRestApiImplFunction(
                    netSourceAnno, it.ksClass,
                    netResultClass, retrofit,
                    netApiClassName, exceptionClass
                )
            }
        }

        val fileSpec = FileSpec.builder(dataStruct.packageName, "${dataStruct.fileName}NetSource")
//            .addImport("gene.retrofit.anno".toClassName(),"INetResult")
            .addType(interfaceBuilder.build())
            .addType(objectBuilder.build())
            .build()
        fileSpec.writeTo(environment.codeGenerator, false, listOf(dataStruct.ksFile))
    }

    private fun log(log: String) {
        environment.logger.warn(log)
    }
}