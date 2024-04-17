package com.techyourchance.architecture.question

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteQuestionsUseCase(
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    fun observeFavorites(): Flow<List<FavoriteQuestion>> {
        return favoriteQuestionDao.observe()
    }
}