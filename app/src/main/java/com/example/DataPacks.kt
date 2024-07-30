package com.example

import gene.net.repository.NetSource


@NetSource("post", "/{user}/{id}/datas")
data class DataDTO(val name: String)