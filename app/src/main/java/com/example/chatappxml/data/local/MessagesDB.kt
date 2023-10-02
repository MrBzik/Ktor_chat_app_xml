package com.example.chatappxml.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatappxml.data.local.MessagesDAO
import com.example.chatappxml.data.local.entities.MessageDb

@Database(
    entities = [MessageDb::class],
    version = 1
)
abstract class MessagesDB : RoomDatabase() {
    abstract val dao : MessagesDAO
}