package com.example.ksptt.auto

/**
 * 删除此无注解非相关联文件不会导致ksp扫描所有相关联源文件，从而不会重新生成文件
 * 但是任意修改，新增任意文件，都会触发ksp扫描所有相关联源文件，会扫出所有注解，会重新生成文件
 */
class NoAutoService {
    fun test11() {}
}