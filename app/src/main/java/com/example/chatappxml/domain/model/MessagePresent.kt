package com.example.chatappxml.domain.model

data class MessagePresent(
    val username : String,
    val date : String,
    val message : String,
    val userId : Long,
    val receiver : Long,
)
