package com.techyourchance.architecture.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.techyourchance.architecture.BuildConfig
import com.techyourchance.architecture.common.database.MyRoomDatabase
import com.techyourchance.architecture.networking.StackoverflowApi
import com.techyourchance.architecture.screens.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val retrofit by lazy {
        val httpClient = OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            build()
        }

        Retrofit.Builder()
            .baseUrl("http://api.stackexchange.com/2.3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
    }

    private val stackoverflowApi by lazy {
        retrofit.create(StackoverflowApi::class.java)
    }

    private val myRoomDatabase by lazy {
        Room.databaseBuilder(
            this@MainActivity,
            MyRoomDatabase::class.java,
            "MyDatabase"
        ).build()
    }

    private val favoriteQuestionDao by lazy {
        myRoomDatabase.favoriteQuestionDao
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MainScreen(
                    stackoverflowApi = stackoverflowApi,
                    favoriteQuestionDao = favoriteQuestionDao,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

