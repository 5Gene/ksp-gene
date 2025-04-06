package com.example

import gene.retrofit.anno.NetSource

@NetSource("post", "/{user}/{id}/findAll", params = "who,age", list = true)
@NetSource("post", "/{user}/check", params = "who,id")
data class DataDTO(val name: String)