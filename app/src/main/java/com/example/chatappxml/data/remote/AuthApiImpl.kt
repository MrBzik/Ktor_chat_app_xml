package com.example.chatappxml.data.remote

import android.content.SharedPreferences
import android.util.Log
import com.example.chatappxml.data.remote.dto.AuthRequest
import com.example.chatappxml.data.remote.dto.TokenResponse
import com.example.chatappxml.domain.model.EncryptedUserInfo
import com.example.chatappxml.presentation.auth.AuthResult
import com.example.chatappxml.utils.Constants.AUTH_TOKEN
import com.example.chatappxml.utils.Constants.USER_ID
import com.example.chatappxml.utils.Constants.USER_NAME
import com.example.chatappxml.utils.Constants.USER_PASSWORD
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.ConnectException

class AuthApiImpl(
    private val client: HttpClient,
    private val authSharedPref: SharedPreferences,
    private val userInfo: SharedPreferences
) : AuthApi {


    override val authStatus = Channel<AuthResult>()

    override val loadingState = MutableStateFlow(false)

    override suspend fun signUp(request: AuthRequest) {

        try {


            loadingState.value = true


            client.post<HttpResponse> {
                url(AuthApi.Endpoints.SignUp.url)
                contentType(ContentType.Application.Json)
                body = request
            }

            signIn(request)

        } catch (e: Exception) {
            Log.d("CHECKTAGS", e.stackTraceToString())
            handleException(e, "Failed to sign up")
        }
    }

    override suspend fun signIn(request: AuthRequest) {

        try {

            loadingState.value = true

            val token = client.post<TokenResponse> {
                url(AuthApi.Endpoints.SignIn.url)
                contentType(ContentType.Application.Json)
                body = request
            }

            Log.d("CHECKTAGS", "!!!!!!!userId is : ${token.userId}")

            authSharedPref.edit().apply {
                putString(AUTH_TOKEN, token.token)
                putLong(USER_ID, token.userId)
            }.apply()


            userInfo.edit().apply {
                putString(USER_NAME, request.username)
                putString(USER_PASSWORD, request.password)
            }.apply()

            loadingState.value = false
            authStatus.send(AuthResult.Authorized)

        } catch (e: Exception) {
            Log.d("CHECKTAGS", e.stackTraceToString())
            handleException(e, "Failed to login")
        }
    }

    override suspend fun authenticate(): EncryptedUserInfo? {

        try {

            loadingState.value = true

            val token = authSharedPref.getString(AUTH_TOKEN, "no token")

            token?.let {

                val userId = client.get<Long>(AuthApi.Endpoints.Authenticate.url) {
                    header("Authorization", "Bearer $token")
                }

                Log.d("CHECKTAGS", "userId is : $userId")

                authSharedPref.edit().putLong(USER_ID, userId).apply()

                loadingState.value = false
                authStatus.send(AuthResult.Authorized)

            } ?: kotlin.run {
                throw IllegalArgumentException()
            }

        } catch (e: Exception) {
            Log.d("CHECKTAGS", e.stackTraceToString())

            handleException(e, "Authentication required", true)

            val prevUserName = userInfo.getString(USER_NAME, "") ?: ""
            val prevUserPass = userInfo.getString(USER_PASSWORD, "") ?: ""

            return EncryptedUserInfo(prevUserName, prevUserPass)
        }

        return null
    }


    private suspend fun handleException(e: Exception, specific: String, isInitial : Boolean = false) {
        val message = when (e) {
            is ConnectTimeoutException -> "No response from server"
            is ConnectException -> "Problem with internet connection"
            else -> specific
        }

        loadingState.value = false

        if(isInitial)
            authStatus.send(AuthResult.Unauthorized(message))
        else
            authStatus.send(AuthResult.Error(message))
    }

}