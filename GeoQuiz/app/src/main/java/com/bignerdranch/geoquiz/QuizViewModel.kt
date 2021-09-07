package com.bignerdranch.geoquiz

import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel: ViewModel() {
    var currentIndex = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    var questionAnswered = BooleanArray(questionBankSize) {false}
    var questionCheated = BooleanArray(questionBankSize) {false}

    val currentQuestionAnswer: Boolean get() = questionBank[currentIndex].answer
    val currentQuestionText: Int get() = questionBank[currentIndex].textResId
    val questionBankSize: Int get() = questionBank.size
    var cheatedQuestionsNum: Int = 0

    val currentQuestionAnswered: Boolean get() = questionAnswered[currentIndex]
    var currentQuestionCheated: Boolean
        get() = questionCheated[currentIndex]
        set(value) {
            questionCheated[currentIndex] = value
            cheatedQuestionsNum++
        }
    val allQuestionsAnswered: Boolean get() = questionAnswered.all { it }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev(){
        currentIndex = if (currentIndex == 0) questionBank.size - 1 else currentIndex - 1
    }

    fun labelQuestionAsAnswered(){
        questionAnswered[currentIndex] = true
    }
}