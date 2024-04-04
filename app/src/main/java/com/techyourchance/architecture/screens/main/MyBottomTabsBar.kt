package com.techyourchance.architecture.screens.main

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.techyourchance.architecture.screens.BottomTab
import com.techyourchance.architecture.screens.Route


@Composable
fun MyBottomTabsBar(parentController: NavController) {

    val bottomTabsToRootRoutes = remember() {
        mapOf(
            BottomTab.Main to Route.MainTab,
            BottomTab.Favorites to Route.FavoritesTab,
        )
    }

    val navBackStackEntry by parentController.currentBackStackEntryAsState()

    val currentRoute = remember(navBackStackEntry) {
        when(val currentRouteName = navBackStackEntry?.destination?.route) {
            Route.QuestionsListScreen.routeName -> Route.QuestionsListScreen
            Route.QuestionDetailsScreen.routeName -> Route.QuestionDetailsScreen
            Route.FavoriteQuestionsScreen.routeName -> Route.FavoriteQuestionsScreen
            Route.MainTab.routeName -> Route.MainTab
            Route.FavoritesTab.routeName -> Route.FavoritesTab
            null -> null
            else -> throw RuntimeException("unsupported route: $currentRouteName")
        }
    }

    NavigationBar {
        bottomTabsToRootRoutes.keys.forEachIndexed { _, bottomTab ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(bottomTab.icon!!, contentDescription = bottomTab.title) },
                label = { Text(bottomTab.title) },
                selected = currentRoute?.bottomTab == bottomTab,
                onClick = {
                    parentController.navigate(bottomTabsToRootRoutes[bottomTab]!!.routeName) {
                        parentController.graph.startDestinationRoute?.let { startRoute ->
                            popUpTo(startRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}