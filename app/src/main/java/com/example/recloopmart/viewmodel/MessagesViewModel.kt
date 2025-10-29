package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recloopmart.data.Conversation
import com.example.recloopmart.data.ConversationRepository

class MessagesViewModel : ViewModel() {
    private val repo = ConversationRepository()

    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> = _conversations

    fun load() {
        _conversations.value = repo.load()
    }
}



