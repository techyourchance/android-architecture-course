package com.techyourchance.architecture.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.techyourchance.architecture.common.database.MyRoomDatabase
import com.techyourchance.architecture.networking.StackoverflowApi
import com.techyourchance.architecture.screens.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var myRoomDatabase: MyRoomDatabase
    @Inject lateinit var retrofit: Retrofit


    private val stackoverflowApi by lazy {
        retrofit.create(StackoverflowApi::class.java)
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

