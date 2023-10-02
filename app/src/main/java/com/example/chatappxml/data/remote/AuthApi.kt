package com.example.chatappxml.data.remote

import com.example.chatappxml.data.remote.dto.AuthRequest
import com.example.chatappxml.domain.model.EncryptedUserInfo
import com.example.chatappxml.presentation.auth.AuthResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthApi {

    val authStatus: Channel<AuthResult>

    val loadingState : MutableStateFlow<Boolean>

    suspend fun signUp(request: AuthRequest)

    suspend fun signIn(request: AuthRequest)

    suspend fun authenticate(): EncryptedUserInfo?

    companion object {
        const val BASE_URL = "http://192.168.1.165:8080"
    }

    sealed class Endpoints(val url: String) {
        object SignUp : Endpoints("$BASE_URL/signup")
        object SignIn : Endpoints("$BASE_URL/signin")
        object Authenticate : Endpoints("$BASE_URL/authenticate")

    }

}