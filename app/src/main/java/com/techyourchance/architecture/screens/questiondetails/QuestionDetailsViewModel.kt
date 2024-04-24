package com.techyourchance.architecture.screens.questiondetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.question.usecases.ObserveQuestionDetailsUseCase
import com.techyourchance.architecture.question.QuestionWithBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestionDetailsViewModel @Inject constructor(
    private val observeQuestionDetailsUseCase: ObserveQuestionDetailsUseCase,
): ViewModel() {

    sealed class QuestionDetailsResult {
        data object None: QuestionDetailsResult()
        data class Success(val questionDetails: QuestionWithBody): QuestionDetailsResult()
        data object Error: QuestionDetailsResult()
    }

    val questionDetails = MutableStateFlow<QuestionDetailsResult>(QuestionDetailsResult.None)

    suspend fun observeQuestionDetails(questionId: String) {
        withContext(Dispatchers.Main.immediate) {
           observeQuestionDetailsUseCase.observeQuestionDetails(questionId).collect { useCaseResult ->
               val result = when (useCaseResult) {
                   is ObserveQuestionDetailsUseCase.QuestionDetailsResult.Success -> {
                       QuestionDetailsResult.Success(useCaseResult.questionDetails)
                   }
                   is ObserveQuestionDetailsUseCase.QuestionDetailsResult.Error -> {
                       QuestionDetailsResult.Error
                   }
                   else -> {
                       QuestionDetailsResult.None
                   }
               }
               questionDetails.value = result
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("QuestionDetailsViewModel", "onCleared()")
    }
}