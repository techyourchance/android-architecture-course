package com.techyourchance.architecture.networking.question

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestionsListSchema (
    @Json(name = "items") val questions: List<QuestionSchema>,
)