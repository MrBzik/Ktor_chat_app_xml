package com.example.chatappxml.data.local.mappers

import com.example.chatappxml.data.remote.dto.MessageDto
import com.example.chatappxml.data.local.entities.MessageDb
import com.example.chatappxml.domain.model.MessagePresent
import java.text.DateFormat
import java.util.Date


fun MessageDto.toMessageDb() : MessageDb {
    return MessageDb(
        id = id,
        username = username,
        timeStamp = timestamp,
        message = message,
        userId = userId,
        receiver = receiver

    )
}

fun MessageDb.toMessagePresent() : MessagePresent {

    val date = Date(timeStamp)
    val format = DateFormat.getDateTimeInstance()
    val dateString = format.format(date)

    return MessagePresent(
        username = username,
        date = dateString,
        message = message,
        userId = userId,
        receiver = receiver
    )

}