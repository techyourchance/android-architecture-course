package com.techyourchance.architecture.screens.favoritequestions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.screens.Route
import com.techyourchance.architecture.screens.common.composables.QuestionItem


@Composable
fun FavoriteQuestionsScreen(
    favoriteQuestionDao: FavoriteQuestionDao,
    navController: NavHostController,
) {
    val favorites = favoriteQuestionDao.observe().collectAsState(initial = listOf())

    if (favorites.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
        ) {
            items(favorites.value.size) { index ->
                val favoriteQuestion = favorites.value[index]
                QuestionItem(
                    questionId = favoriteQuestion.id,
                    questionTitle = favoriteQuestion.title,
                    onQuestionClicked = {
                        navController.navigate(
                            Route.QuestionDetailsScreen.routeName
                                .replace("{questionId}", favoriteQuestion.id)
                                .replace("{questionTitle}", favoriteQuestion.title)
                        )
                    },
                )
                if (index < favorites.value.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 20.dp),
                        thickness = 2.dp
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "No favorites",
            )
        }

    }

}