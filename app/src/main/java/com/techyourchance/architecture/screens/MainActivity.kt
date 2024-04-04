package com.techyourchance.architecture.screens

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.techyourchance.architecture.BuildConfig
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.database.MyRoomDatabase
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.screens.favoritequestions.FavoriteQuestionsScreen
import com.techyourchance.architecture.screens.main.MainScreen
import com.techyourchance.architecture.screens.main.MyBottomTabsBar
import com.techyourchance.architecture.screens.main.MyTopAppBar
import com.techyourchance.architecture.screens.questiondetails.QuestionDetailsScreen
import com.techyourchance.architecture.screens.questionslist.QuestionsListScreen
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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
                    favoriteQuestionDao = favoriteQuestionDao,
                    stackoverflowApi = stackoverflowApi
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

sealed class Route(val routeName: String, val bottomTab: BottomTab) {
    data object MainTab: Route("mainTab", BottomTab.Main)
    data object FavoritesTab: Route("favoritesTab", BottomTab.Favorites)
    data object QuestionsListScreen: Route("questionsList", BottomTab.Main)
    data object QuestionDetailsScreen: Route("questionDetails/{questionId}/{questionTitle}", BottomTab.Main)
    data object FavoriteQuestionsScreen: Route("favorites", BottomTab.Favorites)
}

sealed class BottomTab(val icon: ImageVector?, var title: String) {
    data object Main : BottomTab(Icons.Rounded.Home, "Home")
    data object Favorites : BottomTab(Icons.Rounded.Favorite, "Favorites")
}

