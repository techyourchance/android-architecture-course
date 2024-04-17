package com.techyourchance.architecture.question

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.networking.StackoverflowApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ObserveQuestionDetailsUseCase(
    private val stackoverflowApi: StackoverflowApi,
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    sealed class QuestionDetailsResult {
        data class Success(val questionDetails: QuestionWithBodySchema, val isFavorite: Boolean): QuestionDetailsResult()
        data object Error: QuestionDetailsResult()
    }

    suspend fun observeQuestionDetails(questionId: String): Flow<QuestionDetailsResult> {
        return withContext(Dispatchers.IO) {
            combine(
                flow = flow {
                    emit(stackoverflowApi.fetchQuestionDetails(questionId))
                },
                flow2 = favoriteQuestionDao.observeById(questionId),
            ) { questionDetails, favoriteQuestion ->
                if (questionDetails != null && questionDetails.questions.isNotEmpty()) {
                    QuestionDetailsResult.Success(
                        questionDetails.questions[0],
                        favoriteQuestion != null
                    )
                } else {
                    QuestionDetailsResult.Error
                }
            }.catch {
                QuestionDetailsResult.Error
            }
        }
    }
}