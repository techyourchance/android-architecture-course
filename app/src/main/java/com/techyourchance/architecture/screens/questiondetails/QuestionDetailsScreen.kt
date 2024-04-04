package com.techyourchance.architecture.screens.questiondetails

import android.text.Html
import android.text.Spanned
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.question.QuestionWithBodySchema

@Composable
fun QuestionDetailsScreen(
    questionId: String,
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
    navController: NavHostController,
) {
    var questionDetails by remember { mutableStateOf<QuestionWithBodySchema?>(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(questionId) {
        try {
            questionDetails = stackoverflowApi.fetchQuestionDetails(questionId)!!.questions[0]
        } catch (e: Exception) {
            isError = true
        }
    }

    val isFavorite by favoriteQuestionDao.observeById(questionId).collectAsState(initial = null)

    val scrollState = rememberScrollState()

    if (questionDetails != null) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            questionDetails?.let {

                Spacer(modifier = Modifier.height(20.dp))

                val spannedTitle: Spanned = Html.fromHtml(it.title, Html.FROM_HTML_MODE_LEGACY)
                Text(
                    text = spannedTitle.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                val spannedBody: Spanned = Html.fromHtml(it.body, Html.FROM_HTML_MODE_LEGACY)
                Text(
                    text = spannedBody.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (isError) {
        AlertDialog(
            text = {
                Text("Ooops, something went wrong")
            },
            onDismissRequest = {
                navController.popBackStack()
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}