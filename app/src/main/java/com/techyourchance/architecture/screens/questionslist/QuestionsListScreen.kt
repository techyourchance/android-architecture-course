package com.techyourchance.architecture.screens.questionslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techyourchance.architecture.common.networking.StackoverflowApi
import com.techyourchance.architecture.question.QuestionSchema
import com.techyourchance.architecture.screens.common.composables.QuestionItem

@Composable
fun QuestionsListScreen(
    stackoverflowApi: StackoverflowApi,
    onQuestionClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var questions by remember { mutableStateOf<List<QuestionSchema>>(listOf()) }

    LaunchedEffect(Unit) {
        questions = stackoverflowApi.fetchLastActiveQuestions(20)!!.questions
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
    ) {
        items(questions.size) { index ->
            val question = questions[index]
            QuestionItem(
                questionId = question.id,
                questionTitle = question.title,
                onQuestionClicked = { onQuestionClicked(question.id, question.title) },
            )
            if (index < questions.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    thickness = 2.dp
                )
            }
        }
    }
}