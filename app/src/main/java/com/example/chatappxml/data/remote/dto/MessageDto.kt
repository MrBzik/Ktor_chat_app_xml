package com.example.chatappxml.data.remote.dto

import com.example.chatappxml.domain.model.MessagePresent
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class MessageDto(
    val timestamp: Long,
    val username: String,
    val message: String,
    val userId: Long,
    val id: String,
    val receiver: Long
) {
    fun toMessagePresentation(): MessagePresent {

        val format = DateFormat.getDateTimeInstance()
        val date = format.format(Date(timestamp))

        return MessagePresent(
            username = username,
            userId = userId,
            message = message,
            date = date,
            receiver = receiver
        )
    }
}
