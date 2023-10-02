package com.example.chatappxml.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessagesPageRequest(
    val receiver: Long,
    val lastMessage: Long
)
