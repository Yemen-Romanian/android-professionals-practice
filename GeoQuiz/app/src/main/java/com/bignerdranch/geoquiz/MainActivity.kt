package com.bignerdranch.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    private var questionAsked =  BooleanArray(questionBank.size) {false}

    private var currentIndex = 0
    private var finalScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        prevButton = findViewById(R.id.prev_button)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            questionAsked[currentIndex] = true
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            checkIfQuestionsAsked()
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            questionAsked[currentIndex] = true
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            checkIfQuestionsAsked()
        }

        nextButton.setOnClickListener { view: View ->
            currentIndex = (currentIndex + 1) % questionBank.size
            updateAnswerButtons()
            updateQuestion()
        }

        prevButton.setOnClickListener { view: View ->
            currentIndex = if (currentIndex == 0) questionBank.size - 1 else currentIndex - 1
            updateAnswerButtons()
            updateQuestion()
        }

        questionTextView.setOnClickListener { view: View ->
            currentIndex = (currentIndex + 1) % questionBank.size
            updateAnswerButtons()
            updateQuestion()
        }

        updateQuestion()
    }

    private fun updateAnswerButtons(){
        if (questionAsked[currentIndex]){
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
        else{
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun checkIfQuestionsAsked(){
        if (questionAsked.all { it }){
            Toast.makeText(this, "Your score is $finalScore", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if (userAnswer == correctAnswer){
            finalScore++
            R.string.correct_toast
        } else{
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}