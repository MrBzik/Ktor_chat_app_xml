package com.example.chatappxml.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.chatappxml.data.local.entities.MessageDb


@Dao
interface MessagesDAO {

    @Upsert
    suspend fun insertNewMessages(messages : List<MessageDb>)

    @Upsert
    suspend fun insertOneNewMessage(message : MessageDb)



    @Query("SELECT * FROM MessageDb  where receiver =:receiver ORDER BY timeStamp DESC")
    fun pagingSource(receiver : Long) : PagingSource<Int, MessageDb>

    @Query("SELECT EXISTS(SELECT * FROM MessageDb WHERE receiver =:receiver)")
    suspend fun checkIfElementsExist(receiver: Long) : Boolean


}