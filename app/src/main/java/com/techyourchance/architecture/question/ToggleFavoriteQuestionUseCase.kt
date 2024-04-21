package com.techyourchance.architecture.question

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
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