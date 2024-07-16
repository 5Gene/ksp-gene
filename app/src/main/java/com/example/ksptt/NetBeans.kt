package com.example.ksptt

import gene.net.repository.NetSource

@NetSource("post", "/user/{id}/combo")
data class Combo(val name: String)

@NetSource("get", "/{user}/{id}/combobuy", true)
data class ComboBuy(val name: String)
