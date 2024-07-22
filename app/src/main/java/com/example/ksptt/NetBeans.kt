package com.example.ksptt

import gene.net.repository.NetSource

@NetSource("post", "/user/{id}/combo")
data class TestDTO(val name: String)

@NetSource("get", "/{user}/{id}/combobuy", true, extra = "jiami")
data class TestBean(val name: String)


data class NetResult<T>(val code: Int, val data: T)
