package com.techyourchance.architecture.screens.main

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.techyourchance.architecture.R
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.question.FavoriteQuestion
import com.techyourchance.architecture.screens.Route
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    favoriteQuestionDao: FavoriteQuestionDao,
    currentNavController: NavHostController,
    parentNavController: NavHostController,
) {
    val scope = rememberCoroutineScope()

    val backstackEntryState = currentNavController.currentBackStackEntryAsState()

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

    CenterAlignedTopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White
                )
            }
        },

        navigationIcon = {
            if (!isRootRoute) {
                IconButton(
                    onClick = {
                        if (!currentNavController.popBackStack()) {
                            parentNavController.popBackStack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "menu items"
                    )
                }
            }
        },

        actions = {
            if (isShowFavoriteButton) {
                IconButton(
                    onClick = {
                        scope.launch {
                            if (isFavoriteQuestion) {
                                favoriteQuestionDao.delete(questionIdAndTitle.first)
                            } else {
                                favoriteQuestionDao.upsert(FavoriteQuestion(questionIdAndTitle.first, questionIdAndTitle.second))
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavoriteQuestion) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
    )
}
