package com.techyourchance.architecture.question.usecases

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.question.FavoriteQuestion
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ToggleFavoriteQuestionUseCase @Inject constructor(
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    fun toggleFavoriteQuestion(questionId: String, questionTitle: String) {
        GlobalScope.launch {
            if (favoriteQuestionDao.getById(questionId) != null) {
                favoriteQuestionDao.delete(questionId)
            } else {
                favoriteQuestionDao.upsert(FavoriteQuestion(questionId, questionTitle))
            }
        }
    }
}