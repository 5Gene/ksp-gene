package com.example.ksptt

import gene.net.repository.INetResult
import gene.net.repository.NetSource

@NetSource("post", "/user/{id}/combo")
data class TestDTO(val name: String)

@NetSource("get", "/{user}/{id}/combobuy", true, extra = "jiami", checkResult = false)
data class TestBean(val name: String)


data class NetResult<T>(val code: Int, val data: T) : INetResult<T> {
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
