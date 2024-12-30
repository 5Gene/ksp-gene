package com.example.ksptt

import gene.retrofit.anno.INetResult
import gene.retrofit.anno.NetSource

@NetSource("post", "/user/{id}/combo")
data class TestDTO(val name: String)

@NetSource("get", "/{user}/{id}/combobuy", true, extra = "jiami", check = false)
data class TestBean(val name: String)


data class NetResult<T>(val code: Int, val data: T) : INetResult<T> {
    override fun code(): Int {
        TODO("Not yet implemented")
    }

    override fun isOk(): Boolean {
        TODO("Not yet implemented")
    }

    override fun message(): String {
        TODO("Not yet implemented")
    }

    override fun body(): T {
        TODO("Not yet implemented")
    }
}
