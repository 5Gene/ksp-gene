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
        """\{(.*?)\}""".toRegex().findAll("/user/{ixxxd}/{id}/combo").forEach {
            println(it.groupValues[1])
        }
        throw NetResultException("cuou")
        assertEquals(4, 2 + 2)
    }
}

class NetResultException(override val message: String) : Exception(message, null, false, false)