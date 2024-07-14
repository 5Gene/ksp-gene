package com.example.ksptt.auto

import com.google.auto.service.AutoService

@AutoService(TestService::class, TaskService::class)
class TwoServiceImpl : TestService, TaskService {
    override fun test(msg: String) {
        println(msg)
    }

    override fun action(tag: String) {
        println(tag)
    }
}

@AutoService(TestService::class)
class MockServiceImpl : TestService {
    override fun test(msg: String) {
        println(msg)
    }
}

//@AutoService(TestService::class)
//class FakeServiceImpl : TestService {
//    override fun test(msg: String) {
//        println(msg)
//    }
//}

//@AutoService(TestService::class)
//class ThiedServiceImpl : TestService {
//    override fun test(msg: String) {
//        println(msg)
//    }
//}

//@AutoService(TestService::class)
//class Thied22ServiceImpl : TestService {
//    override fun test(msg: String) {
//        println(msg)
//    }
//}