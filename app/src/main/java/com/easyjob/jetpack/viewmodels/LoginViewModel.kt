package com.easyjob.jetpack.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyjob.jetpack.repositories.AuthRepository
import com.easyjob.jetpack.repositories.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    val repo: AuthRepository = AuthRepositoryImpl()
): ViewModel() {

    //0. Idle
    //1. Loading
    //2. Error
    //3. Success
    val authState = MutableLiveData(0)

    fun signIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("LoginViewModel", "Starting sign-in process.")
            withContext(Dispatchers.Main) {
                authState.value = 1
                Log.d("LoginViewModel", "Auth state set to loading.")
            }

            val response = repo.signIn(email, password)
            Log.e("LoginViewModel", "$response")

            if(response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    authState.value = 3
                    Log.d("LoginViewModel", "Sign-in successful. Auth state set to success.")
                }
            } else {
                withContext(Dispatchers.Main) {
                    authState.value = 2
                    Log.d("LoginViewModel", "Sign-in failed. Auth state set to error.")
                    Log.d("LoginViewModel", "${authState.value}")
                }
            }
        }
    }
}