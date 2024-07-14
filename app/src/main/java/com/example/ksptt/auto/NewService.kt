package com.example.ksptt.auto

import com.google.auto.service.AutoService

@AutoService(TestService::class)
class NewServiceImpl : TestService {
    override fun test(msg: String) {
        println(msg)
    }
}

fun test() {}