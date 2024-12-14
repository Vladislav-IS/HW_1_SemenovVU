package com.semenov.hw1

import org.junit.Test
import org.junit.Assert.assertEquals
import com.semenov.hw1.ERR_INF
import com.semenov.hw1.ERR_INF
import com.semenov.hw1.RandomNumModel

class UnitTest {
    private val model = RandomNumModel()

    @Test
    fun checkErrorEmpty() {
        assertEquals(
            "empty mean test failed",
            true,
            ERR_EMPTY == model.getRandomNum("", "0.1")
        )

        assertEquals(
            "empty variance test failed",
            true,
            ERR_EMPTY == model.getRandomNum("0.1", "")
        )
    }

    @Test
    fun checkErrorInf() {
        val bigMean = "123456"
        val bigVariance = "654321"
        assertEquals(
            "empty variance test failed",
            true,
            ERR_INF == model.getRandomNum(bigMean, bigVariance)
        )
    }
}
