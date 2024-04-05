package com.techyourchance.architecture.screens.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.screens.BottomTab
import com.techyourchance.architecture.screens.Route
import com.techyourchance.architecture.screens.favoritequestions.FavoriteQuestionsScreen
import com.techyourchance.architecture.screens.questiondetails.QuestionDetailsScreen
import com.techyourchance.architecture.screens.questionslist.QuestionsListScreen


@Composable
fun MainScreen(
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
) {
    val parentNavController = rememberNavController()

    val currentNavController = remember {
        mutableStateOf(parentNavController)
    }


    val navBackStackEntry by parentNavController.currentBackStackEntryAsState()

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

    val currentBottomTab = remember (currentRoute) {
        currentRoute?.bottomTab
    }

    val bottomTabsToRootRoutes = remember() {
        mapOf(
            BottomTab.Main to Route.MainTab,
            BottomTab.Favorites to Route.FavoritesTab,
        )
    }

    val backstackEntryState = currentNavController.value.currentBackStackEntryAsState()


    val isRootRoute = remember(backstackEntryState.value) {
        backstackEntryState.value?.destination?.route == Route.QuestionsListScreen.routeName
    }

    val isShowFavoriteButton = remember(backstackEntryState.value) {
        backstackEntryState.value?.destination?.route == Route.QuestionDetailsScreen.routeName
    }

    val questionIdAndTitle = remember(backstackEntryState.value) {
        if (isShowFavoriteButton) {
            Pair(
                backstackEntryState.value?.arguments?.getString("questionId")!!,
                backstackEntryState.value?.arguments?.getString("questionTitle")!!,
            )
        } else {
            Pair("", "")
        }
    }

    var isFavoriteQuestion by remember { mutableStateOf(false) }

    if (isShowFavoriteButton && questionIdAndTitle.first.isNotEmpty()) {
        // Since collectAsState can't be conditionally called, use LaunchedEffect for conditional logic
        LaunchedEffect(questionIdAndTitle) {
            favoriteQuestionDao.observeById(questionIdAndTitle.first).collect { favoriteQuestion ->
                isFavoriteQuestion = favoriteQuestion != null
            }
        }
    }


    Scaffold(
        topBar = {
            MyTopAppBar(
                favoriteQuestionDao = favoriteQuestionDao,
                isRootRoute = isRootRoute,
                isShowFavoriteButton = isShowFavoriteButton,
                isFavoriteQuestion = isFavoriteQuestion,
                questionIdAndTitle = questionIdAndTitle,
                onBackClicked = {
                    if (!currentNavController.value.popBackStack()) {
                        parentNavController.popBackStack()
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier) {
                MyBottomTabsBar(
                    bottomTabs = bottomTabsToRootRoutes.keys.toList(),
                    currentBottomTab = currentBottomTab,
                    onTabClicked = { bottomTab ->
                        parentNavController.navigate(bottomTabsToRootRoutes[bottomTab]!!.routeName) {
                            parentNavController.graph.startDestinationRoute?.let { startRoute ->
                                popUpTo(startRoute) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        content = { padding ->
            MainScreenContent(
                padding = padding,
                parentNavController = parentNavController,
                stackoverflowApi = stackoverflowApi,
                favoriteQuestionDao = favoriteQuestionDao,
                currentNavController = currentNavController,
            )
        }
    )

}

@Composable
private fun MainScreenContent(
    padding: PaddingValues,
    parentNavController: NavHostController,
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
    currentNavController: MutableState<NavHostController>,
) {
    Surface(
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = 12.dp),
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = parentNavController,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            startDestination = Route.MainTab.routeName,
        ) {
            composable(route = Route.MainTab.routeName) {
                val nestedNavController = rememberNavController()
                currentNavController.value = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.QuestionsListScreen.routeName) {
                    composable(route = Route.QuestionsListScreen.routeName) {
                        QuestionsListScreen(
                            stackoverflowApi = stackoverflowApi,
                            onQuestionClicked = { clickedQuestionId, clickedQuestionTitle ->
                                nestedNavController.navigate(
                                    Route.QuestionDetailsScreen.routeName
                                        .replace("{questionId}", clickedQuestionId)
                                        .replace("{questionTitle}", clickedQuestionTitle)
                                )
                            },
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen.routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            onError = {
                                nestedNavController.popBackStack()
                            }
                        )
                    }
                }
            }

            composable(route = Route.FavoritesTab.routeName) {
                val nestedNavController = rememberNavController()
                currentNavController.value = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.FavoriteQuestionsScreen.routeName) {
                    composable(route = Route.FavoriteQuestionsScreen.routeName) {
                        FavoriteQuestionsScreen(
                            favoriteQuestionDao = favoriteQuestionDao,
                            onQuestionClicked = { favoriteQuestionId, favoriteQuestionTitle ->
                                nestedNavController.navigate(
                                    Route.QuestionDetailsScreen.routeName
                                        .replace("{questionId}", favoriteQuestionId)
                                        .replace("{questionTitle}", favoriteQuestionTitle)
                                )
                            }
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen.routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            onError = {
                                nestedNavController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

