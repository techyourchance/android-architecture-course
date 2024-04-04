package com.techyourchance.architecture.screens.common.composables

import android.text.Html
import android.text.Spanned
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun QuestionItem(
    questionId: String,
    questionTitle: String,
    onQuestionClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {
                onQuestionClicked()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            val spannedTitle: Spanned = Html.fromHtml(questionTitle, Html.FROM_HTML_MODE_LEGACY)
            Text(
                modifier = Modifier
                    .weight(1.8f),
                text = spannedTitle.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}