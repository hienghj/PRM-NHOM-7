package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recloopmart.data.NotificationRepository
import com.example.recloopmart.data.NotificationRow

class NotificationsViewModel : ViewModel() {
    private val repo = NotificationRepository()
    private val _rows = MutableLiveData<List<NotificationRow>>()
    val rows: LiveData<List<NotificationRow>> = _rows

    fun load() { _rows.value = repo.load() }
}


