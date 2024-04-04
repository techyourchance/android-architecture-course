package com.techyourchance.architecture.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.techyourchance.architecture.question.FavoriteQuestion

@Database(
    entities = [
        FavoriteQuestion::class
    ],
    version = 1
)
internal abstract class MyRoomDatabase : RoomDatabase() {

    abstract val favoriteQuestionDao: FavoriteQuestionDao
}