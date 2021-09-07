package com.bignerdranch.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Build
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
private const val CHEATS_USED = "cheats_used"
private const val REQUEST_CODE_CHEAT = 0
private const val NUMBER_OF_CHEATS = 3

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button
    private lateinit var cheatsUsedText: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    private var finalScore = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState: ")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putBooleanArray(QUESTIONS_ANSWERED, quizViewModel.questionAnswered)
        outState.putInt(CHEATS_USED, quizViewModel.cheatedQuestionsNum)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK){
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT){
            quizViewModel.currentQuestionCheated =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            if (quizViewModel.cheatedQuestionsNum == NUMBER_OF_CHEATS){
                cheatButton.isEnabled = false
                cheatsUsedText.setText(R.string.all_cheats_used)
            }
            else{
                cheatsUsedText.text = String.format(resources.getString(R.string.cheats_left_text),
                    NUMBER_OF_CHEATS - quizViewModel.cheatedQuestionsNum)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: OnCreate() called")

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val questionAnswered = savedInstanceState?.getBooleanArray(QUESTIONS_ANSWERED) ?: BooleanArray(quizViewModel.questionBankSize) {false}
        val cheatsUsed = savedInstanceState?.getInt(CHEATS_USED, 0) ?: 0

        quizViewModel.currentIndex = currentIndex
        quizViewModel.questionAnswered = questionAnswered

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        cheatsUsedText = findViewById(R.id.cheats_used_text)

        if (cheatsUsed == NUMBER_OF_CHEATS){
            cheatButton.isEnabled = false
            cheatsUsedText.setText(R.string.all_cheats_used)
        }
        else{
            cheatsUsedText.text = String.format(resources.getString(R.string.cheats_left_text),
                NUMBER_OF_CHEATS - cheatsUsed)
        }

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

        cheatButton.setOnClickListener { view: View ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
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

        val messageResId = when{
            quizViewModel.currentQuestionCheated -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                finalScore++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}