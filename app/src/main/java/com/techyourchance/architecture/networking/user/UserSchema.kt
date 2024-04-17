package com.techyourchance.architecture.networking.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserSchema(
    @Json(name = "display_name") val name: String,
    @Json(name = "profile_image") val imageUrl: String?,
)