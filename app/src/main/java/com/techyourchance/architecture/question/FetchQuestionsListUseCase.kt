package com.techyourchance.architecture.question

import com.techyourchance.architecture.networking.StackoverflowApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchQuestionsListUseCase @Inject constructor(
    private val stackoverflowApi: StackoverflowApi
) {

    private var lastNetworkRequestNano = 0L

    private var questions: List<Question> = emptyList()

    suspend fun fetchLastActiveQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            if (hasEnoughTimePassed()) {
                lastNetworkRequestNano = System.nanoTime()
                questions = stackoverflowApi.fetchLastActiveQuestions(20)!!.questions.map { questionSchema ->
                    Question(
                        questionSchema.id,
                        questionSchema.title
                    )
                }
                questions
            } else {
                questions
            }
        }
    }

    private fun hasEnoughTimePassed(): Boolean {
        return System.nanoTime() - lastNetworkRequestNano > THROTTLE_TIMEOUT_MS * 1_000_000
    }

    companion object {
        private const val THROTTLE_TIMEOUT_MS = 5000L
    }
}