package com.techyourchance.architecture.question

import okio.withLock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionsCache @Inject constructor() {

    private val lock = ReentrantLock()

    private val questions = mutableListOf<QuestionWithBody>()

    fun replaceInCache(question: QuestionWithBody) {
        lock.withLock {
            val existingQuestion = questions.firstOrNull { it.id == question.id }
            if (existingQuestion != null) {
                questions.remove(existingQuestion)
            }
            questions.add(question)
        }
    }

    fun get(questionId: String): QuestionWithBody? {
        return lock.withLock {
            questions.firstOrNull { it.id == questionId }
        }
    }
}