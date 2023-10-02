package com.example.chatappxml.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappxml.MainActivity
import com.example.chatappxml.adapters.ChatMessagesAdapter
import com.example.chatappxml.databinding.FragmentChatScreenBinding
import com.example.chatappxml.presentation.chat.ChatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatScreenFragment : Fragment() {

    lateinit var bind : FragmentChatScreenBinding

    lateinit var messagesAdapter : ChatMessagesAdapter

    val chatViewModel : ChatViewModel by lazy {
        (activity as MainActivity).chatViewModel
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentChatScreenBinding.inflate(layoutInflater)
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        subscribeToMessages()
        sendMessageClickListener()
        onMessageValueChangeListener()
    }



    private fun onMessageValueChangeListener(){

        bind.etSendMessage.apply {
            setText(chatViewModel.messageText.value)

            addTextChangedListener {

                chatViewModel.updateMessageText(it.toString())

            }
        }
    }



    private fun sendMessageClickListener(){

        bind.textFieldSendMessage.setEndIconOnClickListener {

            chatViewModel.sendMessage()

        }

    }



    private fun subscribeToMessages(){

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED){

                chatViewModel.getChatRoomMessages().collectLatest {

                    messagesAdapter.submitData(it)

                }
            }
        }
    }


    private fun setRecyclerView(){

        messagesAdapter = ChatMessagesAdapter()

        bind.rvMessages.apply {
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


}