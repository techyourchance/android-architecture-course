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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun QuestionDetailsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    questionId: String,
    viewModel: QuestionDetailsViewModel = viewModel(factory = viewModelFactory),
    onError: () -> Unit,
) {

    val questionDetailsResult = viewModel.questionDetails.collectAsState().value

    LaunchedEffect(questionId) {
        viewModel.fetchQuestionDetails(questionId)
    }

    val scrollState = rememberScrollState()

    if (questionDetailsResult is QuestionDetailsViewModel.QuestionDetailsResult.Success) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            val spannedTitle: Spanned = Html.fromHtml(questionDetailsResult.questionDetails.title, Html.FROM_HTML_MODE_LEGACY)
            Text(
                text = spannedTitle.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            val spannedBody: Spanned = Html.fromHtml(questionDetailsResult.questionDetails.body, Html.FROM_HTML_MODE_LEGACY)
            Text(
                text = spannedBody.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (questionDetailsResult is QuestionDetailsViewModel.QuestionDetailsResult.Error) {
        AlertDialog(
            text = {
                Text("Ooops, something went wrong")
            },
            onDismissRequest = onError,
            confirmButton = {
                Button(
                    onClick = onError

                ) {
                    Text("OK")
                }
            },
        )
    }
}