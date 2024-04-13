package com.techyourchance.architecture.screens.questionslist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.BuildConfig
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.question.QuestionSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class QuestionsListViewModel: ViewModel() {

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

    val lastActiveQuestions = MutableStateFlow<List<QuestionSchema>>(emptyList())

    suspend fun fetchLastActiveQuestions() {
        withContext(Dispatchers.Main.immediate) {
            val questions = stackoverflowApi.fetchLastActiveQuestions(20)!!.questions
            lastActiveQuestions.value = questions
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("QuestionsListViewModel", "onCleared()")
    }
}