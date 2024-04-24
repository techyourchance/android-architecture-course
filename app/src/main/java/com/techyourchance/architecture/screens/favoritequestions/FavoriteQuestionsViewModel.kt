package com.techyourchance.architecture.screens.favoritequestions

import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.question.usecases.ObserveFavoriteQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteQuestionsViewModel @Inject constructor(
    private val observeFavoriteQuestionsUseCase: ObserveFavoriteQuestionsUseCase,
): ViewModel() {

    val favoriteQuestions = observeFavoriteQuestionsUseCase.observeFavorites()
}