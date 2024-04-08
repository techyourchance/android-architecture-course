package com.techyourchance.architecture.screens.questionslist

import com.techyourchance.architecture.BuildConfig
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.question.QuestionSchema
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class QuestionsListPresenter {

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

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    val lastActiveQuestions = MutableStateFlow<List<QuestionSchema>>(emptyList())

    fun fetchLastActiveQuestions() {
        scope.launch {
            val questions = stackoverflowApi.fetchLastActiveQuestions(20)!!.questions
            lastActiveQuestions.value = questions
        }
    }


}