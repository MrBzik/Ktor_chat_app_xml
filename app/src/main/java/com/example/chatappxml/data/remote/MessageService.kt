package com.example.chatappxml.data.remote

import com.example.chatappxml.data.remote.dto.MessageDto

interface MessageService {


    suspend fun getNewChatMessages(lastMessage: Long, query: Long): List<MessageDto>

    suspend fun getOldChatMessages(lastMessage: Long, query: Long): List<MessageDto>

    suspend fun refreshChatMessages(query: Long): List<MessageDto>

    companion object {
        const val BASE_URL = "http://192.168.1.165:8080"
    }


    sealed class Endpoints(val url: String) {
        object RefreshMessages : Endpoints("$BASE_URL/refresh_messages")
        object GetNewMessages : Endpoints("$BASE_URL/new_messages_page")
        object GetOldMessages : Endpoints("$BASE_URL/old_messages_page")
    }

}