package com.techyourchance.architecture

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteQuestion::class
    ],
    version = 1
)
internal abstract class MyRoomDatabase : RoomDatabase() {

    abstract val favoriteQuestionDao: FavoriteQuestionDao
}