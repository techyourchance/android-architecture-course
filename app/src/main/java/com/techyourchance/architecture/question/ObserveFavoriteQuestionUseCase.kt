package com.techyourchance.architecture.question

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveFavoriteQuestionUseCase @Inject constructor(
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    fun isQuestionFavorite(questionId: String): Flow<Boolean> {
        return favoriteQuestionDao.observeById(questionId).map { favoriteQuestion ->
            favoriteQuestion != null
        }
    }
}