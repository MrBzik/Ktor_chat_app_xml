package com.example.chatappxml.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val userName: String,
    val userId: Long
)
