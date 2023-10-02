package com.example.chatappxml.data.remote

import com.example.chatappxml.data.remote.dto.MessageDto
import com.example.chatappxml.data.remote.dto.UserInfo
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatSocketService {

    val isSessionActive: MutableStateFlow<Int>

    val incomingMessages: Channel<MessageDto>

    val users: MutableStateFlow<List<UserInfo>>

    suspend fun initSession()

    suspend fun sendMessage(text: String, receiver: Long)

    fun observeMessages(): Flow<Frame>

    suspend fun closeSession()

    suspend fun setConnectionStateFlow(state: Int)


    companion object {
        const val BASE_URL = "ws://192.168.1.165:8080"
    }


    sealed class Endpoints(val url: String) {
        object ChatSocket : Endpoints("$BASE_URL/chat-socket")
    }


}