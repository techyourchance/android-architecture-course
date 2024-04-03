package com.techyourchance.architecture.question

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.techyourchance.architecture.user.UserSchema

@JsonClass(generateAdapter = true)
data class QuestionSchema(
    @Json(name = "title") val title: String,
    @Json(name = "question_id") val id: String,
    @Json(name = "owner") val owner: UserSchema,
)