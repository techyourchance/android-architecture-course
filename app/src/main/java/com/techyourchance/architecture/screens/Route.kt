package com.techyourchance.architecture.screens

sealed class Route(val routeName: String) {
    data object MainTab: Route("mainTab")
    data object FavoritesTab: Route("favoritesTab")
    data object QuestionsListScreen: Route("questionsList")

    data class QuestionDetailsScreen(
        val questionId: String = "",
        val questionTitle: String = ""
    ): Route("questionDetails/{questionId}/{questionTitle}") {
        override val navCommand: String
            get() = routeName
                .replace("{questionId}", questionId)
                .replace("{questionTitle}", questionTitle)
    }

    data object FavoriteQuestionsScreen: Route("favorites")


    open val navCommand = routeName
}