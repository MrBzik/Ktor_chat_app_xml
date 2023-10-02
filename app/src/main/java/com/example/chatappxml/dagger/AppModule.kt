package com.example.chatappxml.dagger

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.chatappxml.data.local.MessagesDB
import com.example.chatappxml.data.remote.AuthApi
import com.example.chatappxml.data.remote.AuthApiImpl
import com.example.chatappxml.data.remote.ChatSocketService
import com.example.chatappxml.data.remote.ChatSocketServiceImpl
import com.example.chatappxml.data.remote.MessageService
import com.example.chatappxml.data.remote.MessageServiceImpl
import com.example.chatappxml.utils.Constants.AUTH_SHARED_PREF
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import javax.inject.Named
import javax.inject.Singleton


const val REGULAR = "regular"
const val ENCRYPTED = "encrypted"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesMessagesDB(@ApplicationContext context: Context) : MessagesDB {
        return Room.databaseBuilder(
            context,
            MessagesDB::class.java,
            "messages_db"
        ).build()
    }


    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient {
        return HttpClient(CIO){
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }


    @Provides
    @Singleton
    @Named(REGULAR)
    fun providesAuthPref(@ApplicationContext app : Context) : SharedPreferences{
        return app.getSharedPreferences(AUTH_SHARED_PREF, Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    @Named(ENCRYPTED)
    fun providesEncryptedSharedPref(@ApplicationContext app : Context) : SharedPreferences {

        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return  EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKey,
            app,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    @Provides
    @Singleton
    fun providesMessageService(client: HttpClient, @Named(REGULAR) pref: SharedPreferences) : MessageService {
        return MessageServiceImpl(client, pref)
    }

    @Provides
    @Singleton
    fun providesSocketService(client: HttpClient, @Named(REGULAR) pref: SharedPreferences) : ChatSocketService {
        return ChatSocketServiceImpl(client, pref)
    }


    @Provides
    @Singleton
    fun providesAuthApi(client: HttpClient, @Named(REGULAR)  pref : SharedPreferences, @Named(
        ENCRYPTED
    ) userInfo : SharedPreferences) : AuthApi {
        return AuthApiImpl(client= client, authSharedPref = pref, userInfo = userInfo)
    }




}