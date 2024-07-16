package com.example.ksptt

import gene.net.repository.NetSource

@NetSource("post", "/{user}/{id}/datas")
data class DataPacks(val name: String)