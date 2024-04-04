package com.techyourchance.architecture.screens

import java.util.Base64

sealed class Route(val routeName: String, val routeNavigationCommand: String = routeName) {
    data object MainTab: Route("mainTab")
    data object FavoritesTab: Route("favoritesTab")
    data object QuestionsListScreen: Route("questionsList")
    data class QuestionDetailsScreen(private val questionId: String = "dummy", private val questionTitle: String = "dummy"
    ): Route(
        routeName = "questionDetails/{questionId}/{questionTitle}",
        routeNavigationCommand = "questionDetails/$questionId/${Base64.getUrlEncoder().encodeToString(questionTitle.toByteArray())}"
    )
    data object FavoriteQuestionsScreen: Route("favorites")
}