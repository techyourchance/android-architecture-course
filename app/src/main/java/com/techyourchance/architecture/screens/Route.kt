package com.techyourchance.architecture.screens

sealed class Route(val routeName: String) {
    data object MainTab: Route("mainTab")
    data object FavoritesTab: Route("favoritesTab")
    data object QuestionsListScreen: Route("questionsList")
    data object QuestionDetailsScreen: Route("questionDetails/{questionId}/{questionTitle}")
    data object FavoriteQuestionsScreen: Route("favorites")
}