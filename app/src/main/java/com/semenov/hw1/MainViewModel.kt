package com.semenov.hw1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val model = RandomNumModel()
    val numForTextView = MutableLiveData<Double>()

    fun getRandomNum(meanStr: String, varianceStr: String) {
        val randomNum = model.getRandomNum(meanStr, varianceStr)
        numForTextView.postValue(randomNum)
    }
}
