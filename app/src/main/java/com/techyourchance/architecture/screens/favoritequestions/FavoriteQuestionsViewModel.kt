package com.techyourchance.architecture.screens.favoritequestions

import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.common.database.FavoriteQuestionDao

class FavoriteQuestionsViewModel(
    private val favoriteQuestionDao: FavoriteQuestionDao,
): ViewModel() {

    val favoriteQuestions = favoriteQuestionDao.observe()
}