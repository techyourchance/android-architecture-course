package com.techyourchance.architecture.screens.main

import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.question.usecases.ObserveFavoriteQuestionUseCase
import com.techyourchance.architecture.question.usecases.ToggleFavoriteQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeFavoriteQuestionUseCase: ObserveFavoriteQuestionUseCase,
    private val toggleFavoriteQuestionUseCase: ToggleFavoriteQuestionUseCase,
): ViewModel() {

    fun isQuestionFavorite(questionId: String): Flow<Boolean> {
        return observeFavoriteQuestionUseCase.isQuestionFavorite(questionId)
    }

    fun toggleFavoriteQuestion(questionId: String, questionTitle: String) {
        toggleFavoriteQuestionUseCase.toggleFavoriteQuestion(questionId, questionTitle)
    }


}