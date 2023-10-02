package com.example.chatappxml.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class TokenResponse(
    val token: String,
    val userId: Long
)
