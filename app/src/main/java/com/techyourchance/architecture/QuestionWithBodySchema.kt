package com.techyourchance.architecture

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestionWithBodySchema(
    @Json(name = "title") val title: String,
    @Json(name = "question_id") val id: String,
    @Json(name = "body") val body: String,
    @Json(name = "owner") val owner: UserSchema,
)