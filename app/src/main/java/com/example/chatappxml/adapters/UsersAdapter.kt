package com.example.chatappxml.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappxml.data.remote.dto.UserInfo
import com.example.chatappxml.databinding.ItemUserBinding

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(val bind : ItemUserBinding) : RecyclerView.ViewHolder(bind.root)

    var yourId = -1L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val holder = UserViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        holder.itemView.setOnClickListener {

            onItemClickListener?.let { click ->

                click(listOfUsers[holder.bindingAdapterPosition].userId)
            }
        }

        return holder

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val item = listOfUsers[position]

        val text = if(yourId == item.userId)
            item.userName + " (You)"
        else item.userName

        holder.bind.tvUser.text = text

    }




    override fun getItemCount(): Int {
        return listOfUsers.size
    }



    private var onItemClickListener : ((Long) -> Unit)? = null

    fun setOnClickListener(listener : (Long) -> Unit){
        onItemClickListener = listener
    }


    private val diffCallback = object : DiffUtil.ItemCallback<UserInfo>(){

        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.userId == newItem.userId && oldItem.userName == newItem.userName
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listOfUsers : List<UserInfo>
        get() = differ.currentList
        set(value) = differ.submitList(value)





}