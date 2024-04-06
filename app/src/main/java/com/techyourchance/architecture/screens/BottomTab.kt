package com.techyourchance.architecture.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(val icon: ImageVector?, var title: String) {
    data object Main : BottomTab(Icons.Rounded.Home, "Home")
    data object Favorites : BottomTab(Icons.Rounded.Favorite, "Favorites")
}