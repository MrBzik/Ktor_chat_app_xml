package com.example.chatappxml.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chatappxml.MainActivity
import com.example.chatappxml.databinding.FragmentAuthBinding
import com.example.chatappxml.databinding.FragmentChatRoomsBinding
import com.example.chatappxml.utils.Constants
import com.example.chatappxml.utils.Constants.AUTH_SIGN_IN
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserAuthFragment : Fragment() {

    lateinit var bind : FragmentAuthBinding

    private val usernameViewModel by lazy {

        (activity as MainActivity).usernameViewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentAuthBinding.inflate(layoutInflater)
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        setTextFieldsValues()

        setButtonsClickListener()

        setOnTextFieldsValuesChangeListener()

    }


    private fun setOnTextFieldsValuesChangeListener(){

        bind.etUserName.addTextChangedListener {
            usernameViewModel.updateUsername(it.toString())
        }

        bind.etPassword.addTextChangedListener {
            usernameViewModel.updatePassword(it.toString())
        }

    }


    private fun setTextFieldsValues(){

        bind.etUserName.setText(
            usernameViewModel.usernameText.value
        )

        bind.etPassword.setText(
            usernameViewModel.passwordText.value
        )
    }


    private fun setButtonsClickListener(){
        bind.btnAccept.setOnClickListener {
            usernameViewModel.onSignClick()
        }

        bind.btnToggle.setOnClickListener {
            usernameViewModel.changeAuthOption()
        }
    }



    private fun subscribeToObservers(){


        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED){

                usernameViewModel.authOption.collectLatest {

                    if(it == AUTH_SIGN_IN){
                        bind.tvTitle.text = "Login"
                        bind.btnAccept.text = "Login"
                        bind.btnToggle.text = "Sign up"
                    } else {
                        bind.tvTitle.text = "Create account"
                        bind.btnAccept.text = "Sign up"
                        bind.btnToggle.text = "Login"
                    }
                }
            }
        }

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED){

                usernameViewModel.loadingState.collectLatest {

                    bind.progressBar.isVisible = it

                }
            }
        }
    }

}