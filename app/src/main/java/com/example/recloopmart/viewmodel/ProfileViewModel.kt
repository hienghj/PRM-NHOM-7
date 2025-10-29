package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recloopmart.data.Profile
import com.example.recloopmart.data.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val repo = ProfileRepository()

    private val _profile = MutableLiveData<Profile>()
    val profile: LiveData<Profile> = _profile

    fun load() {
        _profile.value = repo.load()
    }

    fun updateName(name: String) {
        val p = _profile.value ?: return
        _profile.value = p.copy(fullName = name)
    }
}



