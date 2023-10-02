package com.example.chatappxml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chatappxml.databinding.ActivityMainBinding
import com.example.chatappxml.presentation.auth.AuthResult
import com.example.chatappxml.presentation.auth.UsernameViewModel
import com.example.chatappxml.presentation.chat.ChatViewModel
import com.example.chatappxml.ui.ChatRoomsFragment
import com.example.chatappxml.ui.ChatScreenFragment
import com.example.chatappxml.ui.UserAuthFragment
import com.example.chatappxml.utils.Constants.CONNECTION_GOOD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var bind: ActivityMainBinding

    val chatViewModel : ChatViewModel by viewModels()

    val usernameViewModel : UsernameViewModel by viewModels()

    private val chatRoomsFragment by lazy {
        ChatRoomsFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        bind = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        startCollectingConnectionState()

        onAuthSuccess()

        collectAuthResult()

        usernameViewModel.initialAuthAttempt()

    }



    fun navigateToChatScreen(){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, ChatScreenFragment())
            addToBackStack(null)
            commit()
        }
    }


    private fun collectAuthResult(){
        lifecycleScope.launch {
            usernameViewModel.authState.collectLatest { authState ->
                when (authState) {

                    is AuthResult.Authorized -> {
                        chatViewModel.isInitialLaunch = false
                        onAuthSuccess()

                        supportFragmentManager.beginTransaction().apply {

                            replace(R.id.flFragment, chatRoomsFragment)
                            addToBackStack(null)
                            commit()
                        }
                    }

                    is AuthResult.Error -> {

                        Toast.makeText(this@MainActivity, authState.message, Toast.LENGTH_LONG).show()

                    }


                    is AuthResult.Unauthorized -> {

                        Toast.makeText(this@MainActivity, authState.message, Toast.LENGTH_LONG).show()

                        supportFragmentManager.beginTransaction().apply {

                            replace(R.id.flFragment, UserAuthFragment())
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            }
        }
    }



    private fun onAuthSuccess(){

        if(!chatViewModel.isInitialLaunch){
            chatViewModel.initiateNewConnectionState()
        }
    }




    private fun startCollectingConnectionState(){

        lifecycleScope.launch {

            chatViewModel.isConnected.collectLatest {

                if(it == CONNECTION_GOOD){
                    bind.tvConnectedState.apply {
                        text = "Connected"
                        setTextColor(ContextCompat.getColor(this@MainActivity, R.color.connected))
                    }
                } else {
                    bind.tvConnectedState.apply {
                        text = "Not connected"
                        setTextColor(ContextCompat.getColor(this@MainActivity, R.color.not_connected))
                    }
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        chatViewModel.disconnect()
    }


}