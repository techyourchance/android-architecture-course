package com.techyourchance.architecture.screens.favoritequestions

import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.question.ObserveFavoriteQuestionsUseCase

class FavoriteQuestionsViewModel(
    private val favoriteQuestionDao: FavoriteQuestionDao,
): ViewModel() {

    private val observeFavoriteQuestionsUseCase = ObserveFavoriteQuestionsUseCase(favoriteQuestionDao)

    val favoriteQuestions = observeFavoriteQuestionsUseCase.observeFavorites()
}