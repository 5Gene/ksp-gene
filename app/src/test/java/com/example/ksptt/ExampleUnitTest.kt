package com.example.ksptt

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val str = """
            sid=1, cid=35
            sid=1, cid=35
            sid=1, cid=7
            sid=5, cid=64
            sid=4, cid=29
            sid=5, cid=43
            sid=4, cid=28
            sid=11, cid=7
            sid=17, cid=9
            sid=100, cid=4
            sid=267, cid=3
            sid=1, cid=5
            sid=9, cid=1
            sid=32, cid=6
            sid=1, cid=4
            sid=28, cid=1
            sid=2, cid=84
            sid=2, cid=146
            sid=15, cid=9
            sid=1, cid=8
            sid=11, cid=11
            sid=11, cid=22
            sid=1, cid=27
            sid=1, cid=5
            sid=100, cid=5
            sid=100, cid=5
            sid=1, cid=35
            sid=1, cid=99
            sid=4, cid=21
            sid=4, cid=21
            sid=11, cid=6
            sid=25, cid=6
        """.trimIndent()
        str.split("\n").asSequence().distinct().forEach {
            println(it.toString())
        }
        assertEquals(4, 2 + 2)
    }
}