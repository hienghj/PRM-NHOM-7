package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class PaymentMethod { CARD, BANK, EWALLET }

class VipPurchaseViewModel : ViewModel() {
    private val _selected = MutableLiveData(PaymentMethod.CARD)
    val selected: LiveData<PaymentMethod> = _selected

    fun select(method: PaymentMethod) {
        _selected.value = method
    }
}


