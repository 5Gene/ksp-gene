package osp.spark.auto.service

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import java.io.FileWriter

const val AUTO_SERVICE_NAME = "com.google.auto.service.AutoService"

/**
 * - find kspxxxKotlin in tasks
 * - Create a file named `META-INF/services/<interface>`
 * - For each [AutoService] annotated class for this interface
 * - Create an entry in the file
 */
class AutoServiceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoServiceProcessor(environment)
    }
}

class AutoServiceProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger = environment.logger
    private var roundIndex = 0

    override fun process(resolver: Resolver): List<KSAnnotated> {
        roundIndex++
        //多轮的时候process对象是同一个
        ">$roundIndex process ➱ $this".logInfo(logger)

        //https://kotlinlang.org/docs/ksp-incremental.html#how-it-is-implemented
        val symbolsWithAnnotation = resolver.getSymbolsWithAnnotation(AUTO_SERVICE_NAME)
        if (symbolsWithAnnotation.toList().isEmpty()) {
            return emptyList()
        }
        val invalidateAnnotations = symbolsWithAnnotation.filter { !it.validate() }.toList()

        val autoServiceClassAnnotations = symbolsWithAnnotation.filter { it.validate() }.filterIsInstance<KSClassDeclaration>()
        val serviceImplMap = mutableMapOf<String, MutableSet<String>>()
        val originatingFiles = mutableSetOf<KSFile>()
        autoServiceClassAnnotations.forEach {
            "🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰".logInfo(logger)
//          "➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖".logInfo(logger)
            //被注解的完整类名
            val beAnnotatedFullClassName = it.qualifiedName!!.asString()
            ">$roundIndex 类名 > $beAnnotatedFullClassName".logInfo(logger)

            //AutoService只有一个参数 class
            //这个类上的所有注解
            //找到AutoService注解
            val autoServiceAnnotation = it.annotations.find { it.annotationType.resolve().fullClassName() == AUTO_SERVICE_NAME }!!
            //找到AutoService(xx:class)的具体参数，找到完整接口名, 这里只支持一个参数
            val argument = autoServiceAnnotation.arguments.first()
            //每个注解支持多个参数，每个参数(key=value)这里value也支持多个，
            // AutoService(Class<?>[] value())实际上支持多个class
            val serviceFullNames = mutableListOf<String>()
            if (argument.value is KSType) {
                //java中 - @AutoService({TaskService.class})java这样注解 这里拿到的value是List
                //java中 - @AutoService(TaskService.class)java这样注解 这里拿到的value是KSType不是list
                val serviceFullName = (argument.value as KSType).fullClassName()
                serviceFullNames.add(serviceFullName)
                ">$roundIndex 接口名 > $serviceFullName".logWarn(logger)
                serviceImplMap.getOrPut(serviceFullName) {
                    mutableSetOf()
                }.add(beAnnotatedFullClassName)
            } else {
                //kotlin中不管咋样这里都和AutoService的value的签名一样是多个，类型是List
                (argument.value as List<*>).map { it as KSType }.forEach { argType ->
                    //service接口名
                    val serviceFullName = argType.fullClassName()
                    serviceFullNames.add(serviceFullName)
                    ">$roundIndex 接口名 > $serviceFullName".logWarn(logger)
                    serviceImplMap.getOrPut(serviceFullName) {
                        mutableSetOf()
                    }.add(beAnnotatedFullClassName)
                }
            }

            ">$roundIndex @AutoService(${serviceFullNames.joinToString()})".logInfo(logger)
            ">$roundIndex $beAnnotatedFullClassName".logInfo(logger)
            "🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰🟰".logInfo(logger)
//           "➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖".logInfo(logger)
            it.containingFile!!.fileName.logInfo(logger)
            originatingFiles.add(it.containingFile!!)
        }
        if (serviceImplMap.isNotEmpty()) {
            generateServicesFile(resolver, serviceImplMap, originatingFiles.toList())
        }
        return invalidateAnnotations
    }

    private fun generateServicesFile(resolver: Resolver, serviceImpls: Map<String, MutableSet<String>>, originatingFiles: List<KSFile>) {
        generateServicesFileByAggregatingFalse(resolver, serviceImpls, originatingFiles)
//        generateServicesFileByAggregatingTrue(serviceImpls, originatingFiles)
    }

    /**
     * 此方法aggregating=false实现
     * - aggregating=false，【修改|删除】【关联源文件】，会扫描所有关联源文件，会扫出所有注解，重新生成文件，
     *  【新增的如果是注解源文件】，那么只会扫到注解的那个源文件，其他不会扫到
     *
     * **📜-💯 > 所以要针对新增注解文件处理**
     *  - 通过反射获取生成的文件路径
     *  - 读取生成文件的内容，补充新增扫除的内容
     *  - 通过codeGenerator.createNewFile()写入所有内容，并关联新扫除的源文件，之前关联的ksp会记住
     */
    private fun generateServicesFileByAggregatingFalse(resolver: Resolver, serviceImpls: Map<String, MutableSet<String>>, originatingFiles: List<KSFile>) {
        //baseDir = extensionToDirectory(extensionName: String)
        //val file = File(baseDir, path)
        //先读取生成的文件读出所有内容，然后调用createNewFile重新写入，把新增的ksFile关联就好，之前关联的有被记住
        //fileMap清空重新写入
        serviceImpls.forEach { (service, impls) ->
            val toWriteServiceImpls = mutableSetOf<String>()
            val resourceFile = "META-INF/services/$service"
            logger.warn(">$roundIndex ➤  $resourceFile")
            //通过反射获取生成的文件路径
            val generatedFileCache = environment.getGeneratedFileCacheByNameAndExtension("", resourceFile, "")
            "➱ generatedFile from cache >>> $generatedFileCache".logInfo(logger)
            val serviceImplsCache = mutableSetOf<String>()
            generatedFileCache.readLines().forEach {
                if (it.isNotEmpty()) {
                    serviceImplsCache.add(it)
                    "➱ cache >> $it".logInfo(logger)
                }
            }
            //只要判断有缓存和没缓存的情况
            if (serviceImplsCache.isNotEmpty()) {
                //有缓存的情况下只要判断两种场景
                // - 单纯新增
                // - 修改|删除
                //全删除的场景，新定义的缓存没删，不过不影响，终于知道为啥ksp没次执行且有变动都要删之前生成的文件了
                //修改删除相关文件(同时新增)会扫描所有相关文件，会扫到所有注解
                //如果removeAll返回false,表示impls全部不在serviceImplsCache中
                if (!serviceImplsCache.removeAll(impls)) {
                    //这个分支一定是新增了，但是也要区分一下两种情况
                    // - 1 单纯新增，旧的缓存+新增注解，新增注解只会扫除新增的那一个
                    // - 2 删除了所有旧的新增缓存没有的，删除会扫所有注解，以新扫出的为准
                    //上面两种情况都要把新扫描的结果加上
                    toWriteServiceImpls.addAll(impls)

                    //所以这里要确定是不是第二种情况，只要判断旧的文件是否还存在即可
                    val oldClass = serviceImplsCache.random()
                    //文件在，还要看注解是否有, 因为文件存在可能是注解被移出了，检查注解
                    val autoServiceAnnotated = resolver.getClassDeclarationByName(resolver.getKSNameFromString(oldClass))?.annotations?.find {
                        it.annotationType.resolve().fullClassName() == AUTO_SERVICE_NAME
                    }
                    if (autoServiceAnnotated == null) {
                        //classDeclaration==null旧的已经没了说明是第二种情况，就是删除过注解源文件，以新扫描结果为准
                        "➤ 出现删除旧的所有, 扫描出了所有注解,忽略缓存，并新增了部分注解源文件: ${impls.joinToString()}".yellow.logWarn(logger)
                    } else {
                        //旧的还在就说明是单纯新增了，把缓存中旧的类加上即可
                        toWriteServiceImpls.addAll(serviceImplsCache)
                        "➤ 出现新增了部分注解源文件: ${impls.joinToString()}".yellow.logWarn(logger)
                    }
                } else {
                    //缓存中包含新扫出的，说明不是单纯的新增，就是修改|删除了关联文件，
                    //只是修改，那么扫出的注解和缓存的注解一样，但是后续也要写入文件，因为每次执行到这之前生成的文件都会被删除
                    toWriteServiceImpls.addAll(impls)
                    "➤ 可能出现删除|修改关联文件(可能同时有新增注解文件)部分注解源文件, 扫描出了所有注解,忽略缓存 ".yellow.logWarn(logger)
                }
            } else {
                toWriteServiceImpls.addAll(impls)
                "➤ 无缓存，新增注解源文件, 扫描出了所有注解 ".yellow.logWarn(logger)
            }

            val resultContent = toWriteServiceImpls.joinToString(System.lineSeparator())
            logger.warn(service.lookDown)
            if (roundIndex > 1) {
                //非第一轮，说明process自动生成了注解, 要移出上次fileMap的key否则生成文件会报错FileAlreadyExistsException
                val path = environment.getGeneratedPathByNameAndExtension("", resourceFile, "")
                environment.codeGenerator.fileMap().remove(path)
            }

            //aggregating=true 上面代码看此时会把任意变化的文件都加如到关联文件，也就是说任意文件修改都会导致扫描所有关联文件
            // 【新增任意文件】ksp会扫到所有注解，
            // 删除的如果是关联的文件，ksp会扫描所有关联文件，扫出所有注解
            // 修改删除任意文件，ksp会扫描所有关联文件，扫出所有注解

            //aggregating=false
            // 【新增一个注解源文件】，ksp只会扫除新增的注解，
            // 删除的如果是【非关联的文件】，ksp不会扫描所有关联文件，也就是没删除注解文件，ksp业务逻辑不会变动
            // 删除的如果是【关联的文件】，ksp会扫描所有关联文件，扫出所有注解
            // 修改一个【非关联的文件】，ksp会执行，但是不会扫出注解内容，也就是只扫描修改的文件，所以没扫到注解
            // 修改一个【关联的文件】，ksp会执行，会扫描所有关联文件，扫到所有注解
            val constructor = Dependencies::class.java.getDeclaredConstructor(Boolean::class.java, Boolean::class.java, List::class.java)
            constructor.isAccessible = true

            //💯>所以originatingFiles这个参数一定要有，表示生成的这个文件和哪些源文件有关联，当删除的时候会被通知到全部更新
            //先读取生成的文件读出所有内容，然后调用createNewFile重新写入，把新增的ksFile关联就好，之前关联的有被记住
            //每次执行process都会删除生成的文件
            environment.codeGenerator.createNewFile(
                constructor.newInstance(false, false, originatingFiles), "", resourceFile, ""
            ).bufferedWriter().use { writer ->
                toWriteServiceImpls.forEach {
                    writer.write(it)
                    writer.newLine()
                    logger.warn("➤ ➱ ➾ ➜ ➣  $it")
                }
            }
            //back up,因为每次执行process都会删除生成的文件
            generatedFileCache.writeText(resultContent)
            logger.warn(service.lookup)
        }
    }

    /**
     * 此方法aggregating=true实现
     * - aggregating=true, 【修改|新增】【任意文件】【只有删除相关联的注解源文件】都会扫描关联源文件，会扫出所有注解，会重新生成文件,
     *   删除不相关没有注解的文件不会导致扫描所有相关联源文件
     * - aggregating=false，【修改|删除】【关联源文件】，会扫描所有关联源文件，会扫出所有注解，重新生成文件，
     *  【新增的如果是注解源文件】，那么只会扫到注解的那个源文件，其他不会扫到
     */
    private fun generateServicesFileByAggregatingTrue(serviceImpls: Map<String, MutableSet<String>>, originatingFiles: List<KSFile>) {
        serviceImpls.forEach { (service, impls) ->
            val resourceFile = "META-INF/services/$service"
            logger.warn(">$roundIndex ➤  $resourceFile")
            logger.warn(service.lookDown)
            val createdFile = environment.getGeneratedFiles().find { it.name == service }
            if (createdFile != null) {
                //process生成注解后第二轮新增
                FileWriter(createdFile, true).use { writer ->
                    impls.forEach {
                        writer.write(it)
                        writer.write(System.lineSeparator())
                        logger.warn("➤ ➱ $it")
                    }
                }
            } else {
                // if (dependencies.aggregating) {
                //     dependencies.originatingFiles + anyChangesWildcard
                // } else {
                //     dependencies.originatingFiles
                // }
                //aggregating=true 上面代码看此时会把任意变化的文件都加如到关联文件，也就是说任意文件修改都会导致扫描所有关联文件
                // 【新增任意文件】ksp会扫到所有注解，
                // 删除的如果是关联的文件，ksp会扫描所有关联文件，扫出所有注解
                // 修改删除任意文件，ksp会扫描所有关联文件，扫出所有注解

                //aggregating=false
                // 【新增一个注解源文件】，ksp只会扫除新增的注解，
                // 删除的如果是【非关联的文件】，ksp不会扫描所有关联文件，也就是没删除注解文件，ksp业务逻辑不会变动
                // 删除的如果是【关联的文件】，ksp会扫描所有关联文件，扫出所有注解
                // 修改一个【非关联的文件】，ksp会执行，但是不会扫出注解内容，也就是只扫描修改的文件，所以没扫到注解
                // 修改一个【关联的文件】，ksp会执行，会扫描所有关联文件，扫到所有注解
                val constructor = Dependencies::class.java.getDeclaredConstructor(Boolean::class.java, Boolean::class.java, List::class.java)
                constructor.isAccessible = true

                //aggregating=true, originatingFiles，生成的文件需要依赖多个输入文件的时候使用
                // 表示生成的文件和originatingFiles有关只要originatingFiles之一修改删除，就会全部扫描刷新
                // 删除originatingFiles其中之一，会重新全部扫描刷新，不会遗漏，新增任意文件和修改任意文件也会全部扫描
                //aggregating=false, originatingFiles
                // 新增一个AutoService注解的类只会扫描新增的一个，会导致复写整个文件，会丢失之前收集并写好的AutoService丢失
                // 但是如果一个新增的注解只生成一个对应的文件那么没问题比如Arouter
                // 删除一个AutoService如果生成的文件关联(originatingFiles)了被删除的文件那么会重新扫描

                //💯>所以originatingFiles这个参数一定要有，表示生成的这个文件和哪些源文件有关联，当删除的时候会被通知到全部更新
                //baseDir = extensionToDirectory(extensionName: String)
                //val file = File(baseDir, path)
                environment.codeGenerator.createNewFile(
                    constructor.newInstance(false, true, originatingFiles), "", resourceFile, ""
                ).bufferedWriter().use { writer ->
                    impls.forEach {
                        writer.write(it)
                        writer.newLine()
                        logger.warn("➤ ➱ ➾ ➜ ➣  $it")
                    }
                }
            }

            logger.warn(service.lookup)
        }
    }
}


//<editor-fold desc="extensions for kt">
val String.yellow: String
    get() = "\u001B[93m${this}\u001B[0m"

fun KSType.fullClassName() = declaration.qualifiedName!!.asString()

val String.lookDown: String
    get() = "👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇 $this 👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇"

val String.lookup: String
    get() = "👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆 $this 👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆"

//val logLevel = LogLevel.values().first {
//    project.logger.isEnabled(it)
//}
//cfg.logLevel.value(logLevel)

//org.gradle.logging.level=info
fun String.logInfo(logger: KSPLogger) {
    logger.info(this)
}

fun String.logWarn(logger: KSPLogger) {
    logger.warn(this)
}
//</editor-fold>
