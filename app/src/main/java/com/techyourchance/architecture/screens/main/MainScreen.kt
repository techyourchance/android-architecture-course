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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.techyourchance.architecture.question.FavoriteQuestion
import com.techyourchance.architecture.screens.BottomTab
import com.techyourchance.architecture.screens.Route
import com.techyourchance.architecture.screens.favoritequestions.FavoriteQuestionsScreen
import com.techyourchance.architecture.screens.questiondetails.QuestionDetailsScreen
import com.techyourchance.architecture.screens.questionslist.QuestionsListScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    favoriteQuestionDao: FavoriteQuestionDao,
    stackoverflowApi: StackoverflowApi
) {

    val scope = rememberCoroutineScope()

    val parentNavControllerState = remember {
        mutableStateOf<NavHostController?>(null)
    }

    val currentNavControllerState = remember {
        mutableStateOf(parentNavControllerState.value)
    }

    val currentNavController = remember(currentNavControllerState.value) {
        currentNavControllerState.value
    }

    val backstackEntry = currentNavController?.currentBackStackEntryAsState()?.value

    val isRootRoute = remember(backstackEntry) {
        backstackEntry?.destination?.route == Route.QuestionsListScreen.routeName
    }

    val isShowFavoriteButton = remember(backstackEntry) {
        backstackEntry?.destination?.route == Route.QuestionDetailsScreen.routeName
    }

    val questionIdAndTitle = remember(backstackEntry) {
        if (isShowFavoriteButton) {
            Pair(
                backstackEntry?.arguments?.getString("questionId")!!,
                backstackEntry?.arguments?.getString("questionTitle")!!,
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

    val currentRoute = remember(currentNavController) {
        when(val currentRouteName = currentNavController?.currentBackStackEntry?.destination?.route) {
            Route.QuestionsListScreen.routeName -> Route.QuestionsListScreen
            Route.QuestionDetailsScreen.routeName -> Route.QuestionDetailsScreen
            Route.FavoriteQuestionsScreen.routeName -> Route.FavoriteQuestionsScreen
            Route.MainTab.routeName -> Route.MainTab
            Route.FavoritesTab.routeName -> Route.FavoritesTab
            null -> null
            else -> throw RuntimeException("unsupported route: $currentRouteName")
        }
    }

    val bottomTabsToRootRoutes = remember() {
        mapOf(
            BottomTab.Main to Route.MainTab,
            BottomTab.Favorites to Route.FavoritesTab,
        )
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                isRootRoute = isRootRoute,
                isShowFavoriteButton = isShowFavoriteButton,
                isFavoriteQuestion = isFavoriteQuestion,
                onBackClicked = {
                    currentNavController?.let {
                        if (!it.popBackStack()) {
                            parentNavControllerState.value?.popBackStack()
                        }
                    }
                },
                onFavoriteClicked = {
                    scope.launch {
                        if (isFavoriteQuestion) {
                            favoriteQuestionDao.delete(questionIdAndTitle.first)
                        } else {
                            favoriteQuestionDao.upsert(FavoriteQuestion(questionIdAndTitle.first, questionIdAndTitle.second))
                        }
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier) {
                MyBottomTabsBar(
                    bottomTabs = bottomTabsToRootRoutes.keys.toList(),
                    selectedBottomTab = currentRoute?.bottomTab,
                    onBottomTabSelected = {
                        parentNavControllerState.value?.navigate(bottomTabsToRootRoutes[it]!!.routeName) {
                            parentNavControllerState.value?.graph?.startDestinationRoute?.let { startRoute ->
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
            MainContent(
                padding = padding,
                stackoverflowApi = stackoverflowApi,
                favoriteQuestionDao = favoriteQuestionDao,
                parentNavControllerState = parentNavControllerState,
                currentNavControllerState = currentNavControllerState,
            )
        }
    )
}


@Composable
private fun MainContent(
    padding: PaddingValues,
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
    parentNavControllerState: MutableState<NavHostController?>,
    currentNavControllerState: MutableState<NavHostController?>,
) {

    val parentNavController = rememberNavController()
    parentNavControllerState.value = parentNavController

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
                currentNavControllerState.value = nestedNavController
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
                            navController = nestedNavController,
                        )
                    }
                }

            }

            composable(route = Route.FavoritesTab.routeName) {
                val nestedNavController = rememberNavController()
                currentNavControllerState.value = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.FavoriteQuestionsScreen.routeName) {
                    composable(route = Route.FavoriteQuestionsScreen.routeName) {
                        FavoriteQuestionsScreen(
                            favoriteQuestionDao = favoriteQuestionDao,
                            navController = nestedNavController
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen.routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            navController = nestedNavController
                        )
                    }
                }
            }
        }
    }
}
