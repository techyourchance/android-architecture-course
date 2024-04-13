package com.techyourchance.architecture.screens.questionslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techyourchance.architecture.screens.common.composables.QuestionItem


@Composable
fun QuestionsListScreen(
    viewModel: QuestionsListViewModel = viewModel(),
    onQuestionClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val questions = viewModel.lastActiveQuestions.collectAsState()

    LaunchedEffect(Unit) {
        if (questions.value.isEmpty()) {
            viewModel.fetchLastActiveQuestions()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
    ) {
        items(questions.value.size) { index ->
            val question = questions.value[index]
            QuestionItem(
                questionId = question.id,
                questionTitle = question.title,
                onQuestionClicked = { onQuestionClicked(question.id, question.title) },
            )
            if (index < questions.value.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    thickness = 2.dp
                )
            }
        }
    }
}