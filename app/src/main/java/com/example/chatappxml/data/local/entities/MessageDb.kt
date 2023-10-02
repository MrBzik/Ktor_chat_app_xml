package com.example.chatappxml.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageDb(
    @PrimaryKey
    val id : String,
    val username : String,
    val timeStamp : Long,
    val message : String,
    val userId : Long,
    val receiver : Long
)
