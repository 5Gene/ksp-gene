package com.example

import gene.net.anno.NetSource


@NetSource("post", "/{user}/{id}/data2")
data class DataDTO(val name: String)