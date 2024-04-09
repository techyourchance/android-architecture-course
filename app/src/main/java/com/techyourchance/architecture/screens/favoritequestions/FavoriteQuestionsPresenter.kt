package com.techyourchance.architecture.screens.favoritequestions

import androidx.room.Room
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.database.MyRoomDatabase

class FavoriteQuestionsPresenter(
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    val favoriteQuestions = favoriteQuestionDao.observe()
}