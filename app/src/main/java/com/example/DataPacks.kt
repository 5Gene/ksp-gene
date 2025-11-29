package com.example

import gene.retrofit.anno.NetSource

@NetSource("post", "/{user}/{id}/findAll", params = "who,age", list = true)
@NetSource("post", "/{user}/check", params = "who,id")
@NetSource("get", "/{user}/tes", check = false)
@NetSource("get", "/{user}/tes2", params = "who,id", check = false)
@NetSource("post", "/{user}/obj", nullable = true)
data class DataDTO(val name: String)