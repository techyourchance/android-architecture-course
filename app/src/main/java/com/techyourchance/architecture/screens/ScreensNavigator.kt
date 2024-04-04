package com.techyourchance.architecture.screens

import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScreensNavigator() {

    val selectedTab = mutableStateOf<BottomTab?>(null)

    private var parentNavControllerObserveJob: Job? = null

    var parentNavController: NavHostController? = null
        set(value) {
            parentNavControllerObserveJob?.cancel()
            parentNavControllerObserveJob = GlobalScope.launch(Dispatchers.Main) {
                value?.currentBackStackEntryFlow?.map { backstackEntry ->
                    val route = getRouteForName(backstackEntry.destination.route)
                    bottomTabsToRootRoutes.firstNotNullOf {
                        if (it.value == route) { it.key } else { null }
                    }
                }?.collect {
                    selectedTab.value = it
                }
            }
            field = value
        }

    val currentBackStackEntry = mutableStateOf<NavBackStackEntry?>(null)
    val currentRoute = mutableStateOf<Route?>(null)

    private var currentNavControllerObserveJob: Job? = null

    var currentNavController: NavHostController? = null
        set(value) {
            currentNavControllerObserveJob?.cancel()
            currentNavControllerObserveJob = GlobalScope.launch(Dispatchers.Main) {
                value?.currentBackStackEntryFlow?.map { backstackEntry ->
                    currentBackStackEntry.value = backstackEntry
                    getRouteForName(backstackEntry.destination.route)
                }?.collect {
                    currentRoute.value = it
                }
            }
            field = value
        }

    private val bottomTabsToRootRoutes = mapOf(
        BottomTab.Main to Route.MainTab,
        BottomTab.Favorites to Route.FavoritesTab,
    )

    val bottomTabs = bottomTabsToRootRoutes.keys.toList()

    private fun getRouteForName(routeName: String?): Route? {
        return when(routeName) {
            Route.QuestionsListScreen.routeName -> Route.QuestionsListScreen
            Route.QuestionDetailsScreen().routeName -> Route.QuestionDetailsScreen()
            Route.FavoriteQuestionsScreen.routeName -> Route.FavoriteQuestionsScreen
            Route.MainTab.routeName -> Route.MainTab
            Route.FavoritesTab.routeName -> Route.FavoritesTab
            null -> null
            else -> throw RuntimeException("unsupported route: $routeName")
        }
    }

    fun navigateBack() {
        currentNavController?.let {
            if (!it.popBackStack()) {
                parentNavController?.popBackStack()
            }
        }
    }

    fun navigateToTab(bottomTab: BottomTab) {
        parentNavController?.navigate(bottomTabsToRootRoutes[bottomTab]!!.routeName) {
            parentNavController?.graph?.startDestinationRoute?.let { startRoute ->
                popUpTo(startRoute) {
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateTo(route: Route) {
        currentNavController?.navigate(route.routeNavigationCommand)
    }

}