package com.example

import gene.net.anno.NetSource

@NetSource("post", "/{user}/{id}/findAll", params = "who,age")
@NetSource("post", "/{user}/check", params = "who,id")
data class DataDTO(val name: String)