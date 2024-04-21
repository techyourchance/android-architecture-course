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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

