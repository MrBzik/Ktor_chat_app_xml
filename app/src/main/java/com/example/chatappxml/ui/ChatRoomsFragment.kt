package com.example.chatappxml.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappxml.MainActivity
import com.example.chatappxml.R
import com.example.chatappxml.adapters.UsersAdapter
import com.example.chatappxml.data.remote.dto.UserInfo
import com.example.chatappxml.databinding.FragmentChatRoomsBinding
import com.example.chatappxml.presentation.chat.ChatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatRoomsFragment : Fragment() {

    lateinit var bind : FragmentChatRoomsBinding

    lateinit var usersAdapter : UsersAdapter

    val chatViewModel : ChatViewModel by lazy {
        (activity as MainActivity).chatViewModel
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentChatRoomsBinding.inflate(layoutInflater)
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()

        observeUsers()

    }



    private fun observeUsers(){

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED){

                chatViewModel.users.collectLatest {

                    val footer = UserInfo("Chat with everyone", -1)

                    val listWithFooter = it.toMutableList() + footer

                    usersAdapter.listOfUsers = listWithFooter

                }
            }
        }
    }



    private fun setRecyclerView(){

        usersAdapter = UsersAdapter().apply {

            setOnClickListener {

                chatViewModel.onNavigateToChatRoom(it)
                (activity as MainActivity).navigateToChatScreen()

            }

            yourId = chatViewModel.userId

        }

        bind.rvUsers.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }



}