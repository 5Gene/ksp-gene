package osp.spark.auto.service

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

var test = true
fun generateAutoTest(resolver: Resolver, environment: SymbolProcessorEnvironment) {
    if (test) {
        test = false
        environment.codeGenerator.createNewFile(
            Dependencies(false), "service.generate", "GenerateService", "kt"
        ).use {
            it.write(
                """
package service.generate

import com.google.auto.service.AutoService
import com.example.ksptt.auto.TaskService
import com.example.ksptt.auto.TestService
                      
@AutoService(TestService::class, TaskService::class)
class GenerateService : TestService, TaskService {
    override fun test(msg: String) {
        println(msg)
    }

    override fun action(tag: String) {
        println(tag)
    }
}
        """.trimIndent().toByteArray()
            )
        }
    }
}