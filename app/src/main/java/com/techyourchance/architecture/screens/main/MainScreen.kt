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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.screens.Route
import com.techyourchance.architecture.screens.favoritequestions.FavoriteQuestionsScreen
import com.techyourchance.architecture.screens.questiondetails.QuestionDetailsScreen
import com.techyourchance.architecture.screens.questionslist.QuestionsListScreen

@Composable
fun MainScreen(
    favoriteQuestionDao: FavoriteQuestionDao,
    stackoverflowApi: StackoverflowApi
) {

    val parentNavController = rememberNavController()

    val currentNavController = remember {
        mutableStateOf(parentNavController)
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                favoriteQuestionDao = favoriteQuestionDao,
                currentNavController = currentNavController.value,
                parentNavController = parentNavController,
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier) {
                MyBottomTabsBar(parentController = parentNavController)
            }
        },
        content = { padding ->
            MainContent(
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
private fun MainContent(
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
                            navController = nestedNavController,
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
