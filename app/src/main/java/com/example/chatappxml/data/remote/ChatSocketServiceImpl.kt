package com.example.chatappxml.data.remote

import android.content.SharedPreferences
import android.util.Log
import com.example.chatappxml.data.remote.dto.MessageDto
import com.example.chatappxml.data.remote.dto.OutgoingMessage
import com.example.chatappxml.data.remote.dto.UserInfo
import com.example.chatappxml.utils.Constants
import com.example.chatappxml.utils.Constants.CONNECTION_GOOD
import com.example.chatappxml.utils.Constants.CONNECTION_HOLD
import com.example.chatappxml.utils.Constants.CONNECTION_LOST
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

class ChatSocketServiceImpl(
    private val client: HttpClient,
    private val authPref: SharedPreferences
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override val isSessionActive = MutableStateFlow(CONNECTION_HOLD)

    override val users: MutableStateFlow<List<UserInfo>> = MutableStateFlow(emptyList())

    override val incomingMessages: Channel<MessageDto> = Channel()

    override suspend fun setConnectionStateFlow(state: Int) {
        isSessionActive.value = state
    }

    override suspend fun initSession() {

        val token = authPref.getString(Constants.AUTH_TOKEN, "no token")

        try {
            socket = client.webSocketSession {
                url(ChatSocketService.Endpoints.ChatSocket.url)
                header("Authorization", "Bearer $token")
            }

            if (socket?.isActive == true) {

//                incomingFramesFlow = socket!!.incoming.consumeAsFlow()

                isSessionActive.value = CONNECTION_GOOD

                socket?.incoming?.consumeEach { frame ->

                    Log.d("CHECKTAGS", "got message")

                    if (frame is Frame.Text) {

                        val text = frame.readText()

                        if (text[0] == 'M') {

                            val message = text.drop(1)

                            Log.d("CHECKTAGS", "message : $message")

                            val messageDto = Json.decodeFromString<MessageDto>(message)

                            incomingMessages.send(messageDto)
                        } else if (text[0] == 'U') {

                            val message = text.drop(1)

                            val users = Json.decodeFromString<List<UserInfo>>(message)

                            Log.d("CHECKTAGS", users.toString())

                            this.users.value = users

                        }
                    }

                }

                socket?.coroutineContext?.job?.invokeOnCompletion {
                    if (isSessionActive.value == CONNECTION_GOOD)
                        isSessionActive.value = CONNECTION_LOST
                }
            }

        } catch (e: Exception) {
            isSessionActive.value = CONNECTION_LOST
        }
    }

    override suspend fun sendMessage(text: String, receiver: Long) {

        val jsonMessage = Json.encodeToString(OutgoingMessage(message = text, receiver = receiver))

        socket?.send(Frame.Text(jsonMessage))
    }

    override fun observeMessages(): Flow<Frame> {


        return socket!!.incoming.receiveAsFlow()
//            .filter {
//            it is Frame.Text
//        }
//            .map {
//            Json.decodeFromString<Message>((it as Frame.Text)
//                .readText()).toMessagePresentation()
//        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}