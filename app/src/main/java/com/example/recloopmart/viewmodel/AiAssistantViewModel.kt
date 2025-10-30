package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AiAssistantViewModel : ViewModel() {
    private val _menuVisible = MutableLiveData(false)
    val menuVisible: LiveData<Boolean> = _menuVisible

    private val _position = MutableLiveData(Pair(16f, 64f)) // start, bottom margins in dp
    val position: LiveData<Pair<Float, Float>> = _position

    fun toggleMenu() { 
        android.util.Log.d("AiAssistantVM", "toggleMenu called, current value: ${_menuVisible.value}")
        _menuVisible.value = !(_menuVisible.value ?: false) 
        android.util.Log.d("AiAssistantVM", "toggleMenu new value: ${_menuVisible.value}")
    }

    fun setPosition(xDp: Float, yDp: Float) { _position.value = Pair(xDp, yDp) }
}


