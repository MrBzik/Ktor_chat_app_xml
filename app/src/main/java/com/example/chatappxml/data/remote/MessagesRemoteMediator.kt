package com.example.chatappxml.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.chatappxml.data.local.MessagesDB
import com.example.chatappxml.data.local.entities.MessageDb
import com.example.chatappxml.data.local.mappers.toMessageDb
import kotlinx.coroutines.delay


@OptIn(ExperimentalPagingApi::class)
class MessagesRemoteMediator(
    private val db: MessagesDB,
    private val messageService: MessageService,
    private val query: Long
) : RemoteMediator<Int, MessageDb>() {


    override suspend fun initialize(): InitializeAction {
        // If there are already messages in db skip refresh
        return if (db.dao.checkIfElementsExist(query))
            InitializeAction.SKIP_INITIAL_REFRESH
        else InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageDb>
    ): MediatorResult {

        return try {

            val response = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d("CHECKTAGS", "refresh type, query: $query")

                    messageService.refreshChatMessages(query)


                }

                LoadType.PREPEND -> {
                    //add new messages to start of reversed list

                    Log.d("CHECKTAGS", "prepend type")

                    val firstItem = state.firstItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    Log.d("CHECKTAGS", "lastItem timestamp : ${firstItem.timeStamp}")

                    messageService.getNewChatMessages(firstItem.timeStamp, query)


                }

                LoadType.APPEND -> {

                    // add older messages to end of reversed list


                    Log.d("CHECKTAGS", "append type")
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    Log.d("CHECKTAGS", "firstItem timestamp : ${lastItem.timeStamp}")

                    messageService.getOldChatMessages(lastItem.timeStamp, query)

                }
            }

            db.withTransaction {

                Log.d("CHECKTAGS", "response size : ${response.size}")

                db.dao.insertNewMessages(response.map { dto ->
                    dto.toMessageDb()
                })
            }

//
//            if (loadType == LoadType.REFRESH)
//                delay(3000)

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())

        } catch (e: Exception) {
            Log.d("CHECKTAGS", e.stackTraceToString())
            MediatorResult.Error(e)
        }

    }
}