package com.example.traveler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.Injection
import com.example.traveler.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.traveler.data.Result
import com.example.traveler.data.User
import kotlinx.coroutines.tasks.await

class AuthenticationViewModel: ViewModel() {

    private val _userRepository : UserRepository

    init {
        _userRepository = UserRepository(
            FirebaseAuth.getInstance(), //default firebase auth instance from abstract class
            Injection.instance() // returns firebase firestore
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _authResult.value = _userRepository.signUp(email,password,fullName)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = _userRepository.signIn(email,password)
        }
    }

}