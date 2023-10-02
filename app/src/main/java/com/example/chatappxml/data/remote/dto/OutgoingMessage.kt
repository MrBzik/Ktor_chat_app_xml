package com.example.chatappxml.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingMessage(
    val receiver: Long,
    val message: String
)
