package com.example

import gene.net.anno.NetSource


@NetSource("post", "/{user}/{id}/datas")
data class DataDTO(val name: String)