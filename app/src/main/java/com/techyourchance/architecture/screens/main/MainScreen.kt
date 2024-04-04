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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.question.FavoriteQuestion
import com.techyourchance.architecture.screens.Route
import com.techyourchance.architecture.screens.ScreensNavigator
import com.techyourchance.architecture.screens.favoritequestions.FavoriteQuestionsScreen
import com.techyourchance.architecture.screens.questiondetails.QuestionDetailsScreen
import com.techyourchance.architecture.screens.questionslist.QuestionsListScreen
import kotlinx.coroutines.launch
import java.util.Base64

@Composable
fun MainScreen(
    favoriteQuestionDao: FavoriteQuestionDao,
    stackoverflowApi: StackoverflowApi
) {

    val scope = rememberCoroutineScope()

    val screensNavigator = remember {
        ScreensNavigator()
    }

    val currentRoute = remember(screensNavigator.currentRoute.value) {
        screensNavigator.currentRoute.value
    }

    val isRootRoute = remember (currentRoute) {
        currentRoute?.routeName == Route.QuestionsListScreen.routeName
    }

    val isShowFavoriteButton = remember(currentRoute) {
        currentRoute?.routeName == Route.QuestionDetailsScreen().routeName
    }

    val currentBackStackEntry = remember(screensNavigator.currentBackStackEntry.value) {
        screensNavigator.currentBackStackEntry.value
    }

    val questionIdAndTitle = remember(currentBackStackEntry) {
        if (isShowFavoriteButton) {
            val args = currentBackStackEntry?.arguments
            if (args != null) {
                Pair(
                    args.getString ("questionId")!!,
                    String(Base64.getUrlDecoder().decode(args.getString("questionTitle"))!!)
                )
            } else {
                Pair("", "")
            }
        } else {
            Pair("", "")
        }
    }

    var isFavoriteQuestion by remember { mutableStateOf(false) }

    if (isShowFavoriteButton && questionIdAndTitle.first.isNotEmpty()) {
        LaunchedEffect(questionIdAndTitle) {
            favoriteQuestionDao.observeById(questionIdAndTitle.first).collect { favoriteQuestion ->
                isFavoriteQuestion = favoriteQuestion != null
            }
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                isRootRoute = isRootRoute,
                isShowFavoriteButton = isShowFavoriteButton,
                isFavoriteQuestion = isFavoriteQuestion,
                onBackClicked = { screensNavigator.navigateBack() },
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
                    bottomTabs = screensNavigator.bottomTabs,
                    selectedBottomTab = screensNavigator.selectedTab.value,
                    onBottomTabSelected = {
                        screensNavigator.navigateToTab(it)
                    }
                )
            }
        },
        content = { padding ->
            MainContent(
                padding = padding,
                stackoverflowApi = stackoverflowApi,
                favoriteQuestionDao = favoriteQuestionDao,
                screensNavigator = screensNavigator,
            )
        }
    )
}

@Composable
private fun MainContent(
    padding: PaddingValues,
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
    screensNavigator: ScreensNavigator,
) {

    val parentNavController = rememberNavController()
    screensNavigator.parentNavController = parentNavController

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
                screensNavigator.currentNavController = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.QuestionsListScreen.routeName) {
                    composable(route = Route.QuestionsListScreen.routeName) {
                        QuestionsListScreen(
                            stackoverflowApi = stackoverflowApi,
                            onQuestionClicked = { clickedQuestionId, clickedQuestionTitle ->
                                screensNavigator.navigateTo(Route.QuestionDetailsScreen(clickedQuestionId, clickedQuestionTitle))
                            },
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen().routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            onError = { screensNavigator.navigateBack() },
                        )
                    }
                }
            }

            composable(route = Route.FavoritesTab.routeName) {
                val nestedNavController = rememberNavController()
                screensNavigator.currentNavController = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.FavoriteQuestionsScreen.routeName) {
                    composable(route = Route.FavoriteQuestionsScreen.routeName) {
                        FavoriteQuestionsScreen(
                            favoriteQuestionDao = favoriteQuestionDao,
                            onFavoriteQuestionClicked = { clickedQuestionId, clickedQuestionTitle ->
                                screensNavigator.navigateTo(Route.QuestionDetailsScreen(clickedQuestionId, clickedQuestionTitle))
                            }
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen().routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            onError = { screensNavigator.navigateBack() },
                        )
                    }
                }
            }
        }
    }
}
