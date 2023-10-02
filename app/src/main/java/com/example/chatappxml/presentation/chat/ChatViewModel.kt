package com.example.chatappxml.presentation.chat

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.chatappxml.dagger.REGULAR
import com.example.chatappxml.data.local.MessagesDB
import com.example.chatappxml.data.local.mappers.toMessageDb
import com.example.chatappxml.data.local.mappers.toMessagePresent
import com.example.chatappxml.data.remote.ChatSocketService
import com.example.chatappxml.data.remote.MessageService
import com.example.chatappxml.data.remote.MessagesRemoteMediator
import com.example.chatappxml.domain.model.MessagePresent
import com.example.chatappxml.utils.Constants
import com.example.chatappxml.utils.Constants.CONNECTION_GOOD
import com.example.chatappxml.utils.Constants.CONNECTION_HOLD
import com.example.chatappxml.utils.Constants.CONNECTION_INITIATE
import com.example.chatappxml.utils.Constants.CONNECTION_LOST
import com.example.chatappxml.utils.Constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService : MessageService,
    private val chatSocketService : ChatSocketService,
    @Named(REGULAR) private val authPref : SharedPreferences,
    private val db : MessagesDB
) : ViewModel() {


    var isInitialLaunch = true


    val userId by lazy {
        authPref.getLong(Constants.USER_ID, -1)
    }

    private val _messageText = MutableStateFlow("")
    val messageText : StateFlow<String> = _messageText

    val users = chatSocketService.users.asStateFlow()


    val isConnected = chatSocketService.isSessionActive.asStateFlow()

    var receiver : Long = -1


    @OptIn(ExperimentalPagingApi::class)
    fun getChatRoomMessages() : Flow<PagingData<MessagePresent>> {

        Log.d("CHECKTAGS", "returning paging data flow")
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = MessagesRemoteMediator(db, messageService, receiver)
        ) {
            db.dao.pagingSource(receiver)
        }.flow
            .map {pagingData ->
                pagingData.map {
                    it.toMessagePresent()
                }
            }
            .cachedIn(viewModelScope)
    }


    fun onNavigateToChatRoom(receiverId : Long){

        receiver = receiverId

    }


    fun initiateNewConnectionState() = viewModelScope.launch{
        chatSocketService.setConnectionStateFlow(CONNECTION_INITIATE)
    }


    fun updateMessageText(text : String){
        _messageText.value = text
    }



    init {

        viewModelScope.launch {

            chatSocketService.incomingMessages.consumeEach { message ->

                db.dao.insertOneNewMessage(message = message.toMessageDb())

            }
        }



        isConnected.mapLatest {

            Log.d("CHECKTAGS", "isConnected: $it")

            if(it == CONNECTION_INITIATE) {
                connectToServer()

            } else if(it == CONNECTION_LOST){

                while (isConnected.value == CONNECTION_LOST){

                    connectToServer()
                    delay(3000)
                    Log.d("CHECKTAGS", "looping")
                }
            } else {

            }

        }.launchIn(viewModelScope)

    }

    private fun connectToServer() = viewModelScope.launch {

        Log.d("CHECKTAGS", "calling connect to server")

        chatSocketService.initSession()

    }





    fun sendMessage() = viewModelScope.launch {

        Log.d("CHECKTAGS", "on send message connection : ${isConnected.value}")

        if(_messageText.value.isNotBlank() && isConnected.value == CONNECTION_GOOD){
            chatSocketService.sendMessage(text = _messageText.value, receiver = receiver)
            _messageText.value = ""
        }
    }



    fun disconnect() = viewModelScope.launch {
        Log.d("CHECKTAGS", "am i stopping it?")
        chatSocketService.setConnectionStateFlow(CONNECTION_HOLD)
        chatSocketService.closeSession()
    }


    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}