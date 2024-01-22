package com.example.compositionapp.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compositionapp.R
import com.example.compositionapp.data.GameRepositoryImpl
import com.example.compositionapp.domain.entity.GameResult
import com.example.compositionapp.domain.entity.GameSettings
import com.example.compositionapp.domain.entity.Level
import com.example.compositionapp.domain.entity.Question
import com.example.compositionapp.domain.usecase.GenerateQuestionUseCase
import com.example.compositionapp.domain.usecase.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : AndroidViewModel(application) {

    private lateinit var gameSettings: GameSettings

    private val context = application

    private val repository = GameRepositoryImpl
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)

    private val _formattedTimeLiveData = MutableLiveData<String>()
    val formattedTimeLiveData: LiveData<String> get() = _formattedTimeLiveData

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> get() = _question

    private val _percentOfRightAnswer = MutableLiveData<Int>()
    val percentOfRightAnswer: LiveData<Int> get() = _percentOfRightAnswer

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String> get() = _progressAnswers

    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswers: LiveData<Boolean> get() = _enoughCountOfRightAnswers

    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswers: LiveData<Boolean> get() = _enoughPercentOfRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int> get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult> get() = _gameResult

    private var timer: CountDownTimer? = null

    private var countOfRightAnswer = 0
    private var countOfQuestion = 0

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun finishGame() {
        val gameResult = GameResult(
            winner = enoughCountOfRightAnswers.value == true && enoughPercentOfRightAnswers.value == true,
            countOfRightAnswers = countOfRightAnswer,
            countOfQuestions = countOfQuestion,
            gameSettings = gameSettings
        )
        _gameResult.value = gameResult
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswer.value = percent
        _progressAnswers.value = String.format(
            context.resources.getString(R.string.progress_answers),
            countOfRightAnswer,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswers.value = countOfRightAnswer >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        return  ((countOfRightAnswer / countOfQuestion.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswer++
        }
        countOfQuestion++
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTimeLiveData.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }

}

class GameViewModelFactory(
    private val application: Application,
    private val level: Level
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(application, level) as T
        }
        throw RuntimeException("Unknown view model class $modelClass")
    }
}