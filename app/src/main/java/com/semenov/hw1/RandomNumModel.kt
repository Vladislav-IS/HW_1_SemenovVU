package com.semenov.hw1

import java.util.Random
import kotlin.math.exp
import kotlin.math.sqrt

const val ERR_EMPTY = -1.0
const val ERR_INF = -2.0

class RandomNumModel {
    private val rng = Random()

    fun getRandomNum(meanStr: String, varianceStr: String): Double {
        if (meanStr.isEmpty() || varianceStr.isEmpty())
            return ERR_EMPTY

        val mean = meanStr.toDouble()
        val std = sqrt(varianceStr.toDouble())
        val randomNum = exp(std * rng.nextGaussian() + mean)

        return if (randomNum.isInfinite()) ERR_INF else randomNum
    }
}

