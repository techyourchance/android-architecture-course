package com.techyourchance.architecture.question.usecases

import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.networking.StackoverflowApi
import com.techyourchance.architecture.question.QuestionWithBody
import com.techyourchance.architecture.question.QuestionsCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ObserveQuestionDetailsUseCase @Inject constructor(
    private val stackoverflowApi: StackoverflowApi,
    private val favoriteQuestionDao: FavoriteQuestionDao,
    private val questionsCache: QuestionsCache,
) {

    sealed class QuestionDetailsResult {
        data class Success(val questionDetails: QuestionWithBody): QuestionDetailsResult()
        data object Error: QuestionDetailsResult()
    }

    suspend fun observeQuestionDetails(questionId: String): Flow<QuestionDetailsResult> {
        return withContext(Dispatchers.IO) {
            combine(
                flow = flow {
                    emit(questionsCache.get(questionId) ?: fetchFromNetwork(questionId))
                },
                flow2 = favoriteQuestionDao.observeById(questionId),
            ) { questionDetails, favoriteQuestion ->
                if (questionDetails != null) {
                    val questionWithBody = questionDetails.copy(isFavorite = favoriteQuestion != null)
                    questionsCache.replaceInCache(questionWithBody)
                    QuestionDetailsResult.Success(questionWithBody)
                } else {
                    QuestionDetailsResult.Error
                }
            }.catch {
                QuestionDetailsResult.Error
            }
        }
    }

    private suspend fun fetchFromNetwork(questionId: String): QuestionWithBody? {
        val schema = stackoverflowApi.fetchQuestionDetails(questionId)
        return if (schema != null && schema.questions.isNotEmpty()) {
            schema.questions[0].run {
                QuestionWithBody(id, title, body, false)
            }
        } else {
            null
        }
    }
}