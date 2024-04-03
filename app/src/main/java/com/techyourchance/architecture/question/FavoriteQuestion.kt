package com.techyourchance.architecture.question

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteQuestion(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
)