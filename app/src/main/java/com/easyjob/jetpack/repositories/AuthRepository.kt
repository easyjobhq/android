package com.easyjob.jetpack.repositories

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.easyjob.jetpack.services.AuthService
import com.easyjob.jetpack.services.AuthServiceImpl
import com.easyjob.jetpack.services.LoginRequest
import com.easyjob.jetpack.services.LoginResponse
import com.easyjob.jetpack.services.RegisterResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File


interface AuthRepository {
    suspend fun signIn(email: String, password: String): Response<LoginResponse>

    suspend fun signUpClient(
        name: String,
        last_name: String,
        email: String,
        phone_number: String,
        password: String,
        option: String,
        imageUri: Uri,
        contentResolver: ContentResolver
    ): Response<RegisterResponse>

    suspend fun signUpProfessional(
        name: String,
        last_name: String,
        email: String,
        phone_number: String,
        password: String,
        option: String,
        imageUri: Uri,
        service_id: String,
        language_id: String,
        city_id: String,
        speciality_id: String,
        contentResolver: ContentResolver
    ): Response<RegisterResponse>
}

class AuthRepositoryImpl(
    val authService: AuthService = AuthServiceImpl()
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): Response<LoginResponse> {
        val res = authService.loginWithEmailAndPasswordClient(LoginRequest(email, password))
        Log.e(">>>", "The response is: ${res.body()}")
        return res
    }

    override suspend fun signUpClient(
        name: String,
        last_name: String,
        email: String,
        phone_number: String,
        password: String,
        option: String,
        imageUri: Uri,
        contentResolver: ContentResolver
    ): Response<RegisterResponse> {
        // Convert fields to RequestBody
        val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val lastNamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), last_name)
        val emailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val phoneNumberPart = RequestBody.create("text/plain".toMediaTypeOrNull(), phone_number)
        val passwordPart = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

        val imageName = "client_image"

        val imageStream = contentResolver.openInputStream(imageUri)
        val imagePart = imageStream?.let {
            MultipartBody.Part.createFormData(
                imageName,
                File(imageUri.path!!).name,
                it.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        val res = imagePart?.let {
            authService.registerClient(
                namePart,
                lastNamePart,
                emailPart,
                phoneNumberPart,
                passwordPart,
                it,
            )
        }!!

        Log.e(">>>", "The response is: $res")
        Log.e(">>>", "The response is: ${res.body()}")
        return res
    }

    override suspend fun signUpProfessional(
        name: String,
        last_name: String,
        email: String,
        phone_number: String,
        password: String,
        option: String,
        imageUri: Uri,
        service_id: String,
        language_id: String,
        city_id: String,
        speciality_id: String,
        contentResolver: ContentResolver
    ): Response<RegisterResponse> {

        // Convert fields to RequestBody
        val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val lastNamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), last_name)
        val emailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val phoneNumberPart = RequestBody.create("text/plain".toMediaTypeOrNull(), phone_number)
        val passwordPart = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val servicePart =
            RequestBody.create("text/plain".toMediaTypeOrNull(), service_id)
        val languagePart =
            RequestBody.create("text/plain".toMediaTypeOrNull(), language_id)
        val cityPart = RequestBody.create("text/plain".toMediaTypeOrNull(), city_id)
        val specialityPart =
            RequestBody.create("text/plain".toMediaTypeOrNull(), speciality_id)

        val imageName = "professional_image"

        val imageStream = contentResolver.openInputStream(imageUri)
        val imagePart = imageStream?.let {
            MultipartBody.Part.createFormData(
                imageName,
                File(imageUri.path!!).name,
                it.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        val res = imagePart?.let {
            authService.registerProfessional(
                namePart,
                lastNamePart,
                emailPart,
                phoneNumberPart,
                passwordPart,
                it,
                servicePart,
                languagePart,
                cityPart,
                specialityPart
            )
        }!!


        Log.e(">>>", "The response is: $res")
        Log.e(">>>", "The response body is: ${res.body()}")
        return res
    }

}