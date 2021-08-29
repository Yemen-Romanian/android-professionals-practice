package com.bignerdranch.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val QUESTIONS_ANSWERED = "questions_answered"

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    private var finalScore = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState: ")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putBooleanArray(QUESTIONS_ANSWERED, quizViewModel.questionAnswered)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: OnCreate() called")

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val questionAnswered = savedInstanceState?.getBooleanArray(QUESTIONS_ANSWERED) ?: BooleanArray(quizViewModel.questionBankSize) {false}

        quizViewModel.currentIndex = currentIndex
        quizViewModel.questionAnswered = questionAnswered

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        prevButton = findViewById(R.id.prev_button)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            quizViewModel.labelQuestionAsAnswered()
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            checkIfQuestionsAsked()
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            quizViewModel.labelQuestionAsAnswered()
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            checkIfQuestionsAsked()
        }

        if (quizViewModel.currentQuestionAnswered){
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }

        nextButton.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateAnswerButtons()
            updateQuestion()
        }

        prevButton.setOnClickListener { view: View ->
            quizViewModel.moveToPrev()
            updateAnswerButtons()
            updateQuestion()
        }

        questionTextView.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateAnswerButtons()
            updateQuestion()
        }

        updateQuestion()
    }

    private fun updateAnswerButtons(){
        if (quizViewModel.currentQuestionAnswered){
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
        else{
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun checkIfQuestionsAsked(){
        if (quizViewModel.allQuestionsAnswered){
            Toast.makeText(this, "Your score is $finalScore", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if (userAnswer == correctAnswer){
            finalScore++
            R.string.correct_toast
        } else{
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}