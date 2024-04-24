package com.techyourchance.architecture.question.usecases

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.question.FavoriteQuestion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteQuestionsUseCase @Inject constructor(
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    fun observeFavorites(): Flow<List<FavoriteQuestion>> {
        return favoriteQuestionDao.observe()
    }
}