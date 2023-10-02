package com.example.chatappxml.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappxml.data.remote.AuthApi
import com.example.chatappxml.data.remote.dto.AuthRequest
import com.example.chatappxml.utils.Constants.AUTH_SIGN_IN
import com.example.chatappxml.utils.Constants.AUTH_SIGN_UP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val authApi: AuthApi
) : ViewModel() {



    val authState = authApi.authStatus.receiveAsFlow()

    val authOption = MutableStateFlow(AUTH_SIGN_IN)


    val loadingState = authApi.loadingState.asStateFlow()


    private val _usernameText = MutableStateFlow("")
    val usernameText : StateFlow<String> = _usernameText

    fun updateUsername(text : String){
        _usernameText.value = text
    }


    private val _passwordText = MutableStateFlow("")
    val passwordText : StateFlow<String> = _passwordText

    fun updatePassword(text : String){
        _passwordText.value = text
    }



    fun onSignClick(){
        if(authOption.value == AUTH_SIGN_UP)
            signUp()
        else signIn()
    }

    fun changeAuthOption(){

        Log.d("CHECKTAGS", "updating auth option")

        if(authOption.value == AUTH_SIGN_UP)
            authOption.value = AUTH_SIGN_IN
        else authOption.value = AUTH_SIGN_UP
    }


    private fun provideAuthRequest() : AuthRequest? {

        return if(_usernameText.value.isNotBlank() && _passwordText.value.isNotBlank())
            AuthRequest(username = _usernameText.value, password = _passwordText.value)
        else null

    }


    private fun signUp() = viewModelScope.launch {
        provideAuthRequest()?.let {
            authApi.signUp(it)
        }
    }

    private fun signIn() = viewModelScope.launch {
        provideAuthRequest()?.let {
            authApi.signIn(it)
        }
    }


    fun initialAuthAttempt(){
        viewModelScope.launch {
            authApi.authenticate()?.let {
                updateUsername(it.userName)
                updatePassword(it.userPassword)
            }
        }
    }


}