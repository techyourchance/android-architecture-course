package com.techyourchance.architecture.screens.questionslist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.question.FetchQuestionsListUseCase
import com.techyourchance.architecture.question.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class QuestionsListViewModel: ViewModel() {

    private val fetchQuestionsListUseCase = FetchQuestionsListUseCase()

    val lastActiveQuestions = MutableStateFlow<List<Question>>(emptyList())

    suspend fun fetchLastActiveQuestions(forceUpdate: Boolean = false) {
        withContext(Dispatchers.Main.immediate) {
            if (forceUpdate || lastActiveQuestions.value.isEmpty()) {
                lastActiveQuestions.value = fetchQuestionsListUseCase.fetchLastActiveQuestions()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("QuestionsListViewModel", "onCleared()")
    }

}