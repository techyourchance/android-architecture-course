package com.techyourchance.architecture.screens

import android.os.Bundle
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScreensNavigator {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    private lateinit var parentNavController: NavHostController
    private lateinit var nestedNavController: NavHostController

    private var nestedNavControllerObserveJob: Job? = null
    private var parentNavControllerObserveJob: Job? = null

    val currentBottomTab = MutableStateFlow<BottomTab?>(null)
    val currentRoute = MutableStateFlow<Route?>(null)
    val isRootRoute = MutableStateFlow(false)

    fun setParentNavController(navController: NavHostController) {
        parentNavController = navController

        parentNavControllerObserveJob?.cancel()
        parentNavControllerObserveJob = scope.launch {
            navController.currentBackStackEntryFlow.map { backStackEntry ->
                val bottomTab = when (val routeName = backStackEntry.destination.route) {
                    Route.MainTab.routeName -> BottomTab.Main
                    Route.FavoritesTab.routeName -> BottomTab.Favorites
                    null -> null
                    else -> throw RuntimeException("unsupported bottom tab: $routeName")
                }
                currentBottomTab.value = bottomTab
            }.collect()
        }
    }

    fun setNestedNavController(navController: NavHostController) {
        nestedNavController = navController

        nestedNavControllerObserveJob?.cancel()
        nestedNavControllerObserveJob = scope.launch {
            navController.currentBackStackEntryFlow.map { backStackEntry ->
                val route = when (val routeName = backStackEntry.destination.route) {
                    Route.MainTab.routeName -> Route.MainTab
                    Route.FavoritesTab.routeName -> Route.FavoritesTab
                    Route.QuestionsListScreen.routeName -> Route.QuestionsListScreen
                    Route.QuestionDetailsScreen().routeName -> {
                        val args = backStackEntry.arguments
                        Route.QuestionDetailsScreen(
                            args?.getString("questionId")!!,
                            args?.getString("questionTitle")!!
                        )
                    }
                    Route.FavoriteQuestionsScreen.routeName -> Route.FavoriteQuestionsScreen
                    null -> null
                    else -> throw RuntimeException("unsupported route: $routeName")
                }
                currentRoute.value = route
                isRootRoute.value = route == Route.QuestionsListScreen
            }.collect()
        }

    }

    fun navigateBack() {
        if (!nestedNavController.popBackStack()) {
            parentNavController.popBackStack()
        }
    }

    fun toTab(bottomTab: BottomTab) {
        val route = when(bottomTab) {
            BottomTab.Favorites -> Route.FavoritesTab
            BottomTab.Main -> Route.MainTab
        }
        parentNavController.navigate(route.routeName) {
            parentNavController.graph.startDestinationRoute?.let { startRoute ->
                popUpTo(startRoute) {
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun toRoute(route: Route) {
        nestedNavController.navigate(route.navCommand)
    }


    companion object {
        val BOTTOM_TABS = listOf(BottomTab.Main, BottomTab.Favorites)
    }


}