package com.easyjob.jetpack.viewmodels

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyjob.jetpack.repositories.AuthRepository
import com.easyjob.jetpack.repositories.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class RegisterViewModel(
    val repo: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    //0. Idle
    //1. Loading
    //2. Error
    //3. Success
    val authState = MutableLiveData(0)

    fun signUp(
        name: String,
        last_name: String,
        email: String,
        phone_number: String,
        password: String,
        option: String,
        uri: Uri,
        contentResolver: ContentResolver
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("RegisterViewModel", "Starting sign-up process.")
            withContext(Dispatchers.Main) {
                authState.value = 1
                Log.d("RegisterViewModel", "Auth state set to loading.")
            }

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

            if (bitmap != null) {
                // Convertir la imagen a un archivo temporal o hacer lo que necesites
                val file = File.createTempFile("image", ".jpg")
                // Aquí puedes escribir la lógica para convertir bitmap a un archivo
                // o simplemente usar el inputStream para enviarlo a tu API.

                val response =
                    repo.signUp(name, last_name, email, phone_number, password, option, uri, contentResolver)
                Log.e("RegisterViewModel", "$response")

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        authState.value = 3
                        Log.d("RegisterViewModel", "Sign-up successful. Auth state set to success.")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        authState.value = 2
                        Log.d("RegisterViewModel", "Sign-up failed. Auth state set to error.")
                        Log.d("RegisterViewModel", "${authState.value}")
                    }
                }
            } else {
                Log.e("UploadImage", "Bitmap is null, cannot proceed with sign-up.")
                withContext(Dispatchers.Main) {
                    authState.value = 2 // Set error state
                }
            }
        }
    }
}