package com.example.chatappxml.presentation.auth

sealed class AuthResult(val message : String? = null) {
    object Authorized : AuthResult()
    class Unauthorized(message: String) : AuthResult(message)
    class Error(message : String) : AuthResult(message)
}