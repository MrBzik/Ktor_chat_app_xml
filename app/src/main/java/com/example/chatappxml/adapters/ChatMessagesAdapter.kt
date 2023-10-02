package com.example.chatappxml.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappxml.databinding.ItemMessageBinding
import com.example.chatappxml.domain.model.MessagePresent

class ChatMessagesAdapter :
    PagingDataAdapter<MessagePresent, ChatMessagesAdapter.MessageViewHolder>(MessagesComparator) {


    class MessageViewHolder(val bind : ItemMessageBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        return MessageViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val item = getItem(position)!!



        holder.bind.apply {

            tvSender.text = item.username

            tvMessage.text = item.message

            tvDate.text = item.date

        }
    }



    object MessagesComparator : DiffUtil.ItemCallback<MessagePresent>(){

        override fun areItemsTheSame(oldItem: MessagePresent, newItem: MessagePresent): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: MessagePresent, newItem: MessagePresent): Boolean {
            return oldItem.date == newItem.date
        }
    }

}