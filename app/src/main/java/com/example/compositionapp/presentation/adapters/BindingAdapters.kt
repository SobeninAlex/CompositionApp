package com.example.compositionapp.presentation.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.compositionapp.R
import com.example.compositionapp.domain.entity.GameResult

@BindingAdapter("requiredAnswer")
fun bindRequiredAnswer(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_score),
        count
    )
}

@BindingAdapter("scoreAnswer")
fun bindScoreAnswer(textView: TextView, score: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.score_answers),
        score
    )
}

@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, percentage: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_percentage),
        percentage
    )
}

@BindingAdapter("scorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        getPercentOfRightAnswers(gameResult)
    )
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if (countOfQuestions == 0) {
        0
    } else {
        ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}

@BindingAdapter("emojiResult")
fun bindEmojiResult(imageView: ImageView, gameResult: GameResult) {
    imageView.setImageResource(getSmileResourcesId(gameResult))
}
// можно не создавать отдельный адаптер,
// а использовать в xml уже существующий //app:imageResource="@{}

private fun getSmileResourcesId(gameResult: GameResult): Int {
    return if (gameResult.winner) {
        R.drawable.ic_smile
    } else {
        R.drawable.ic_sad
    }
}

@BindingAdapter("answerProgress")
fun bindAnswerProgress(textView: TextView, state: Boolean) {
    textView.setTextColor(getColorByState(state, textView.context))
}

@BindingAdapter("progressBarColor")
fun bindProgressBar(
    progressBar: ProgressBar,
    state: Boolean
) {
    val color = getColorByState(state, progressBar.context)
    progressBar.progressTintList = ColorStateList.valueOf(color)
}

private fun getColorByState(state: Boolean, context: Context): Int {
    val color =  if (state) {
        android.R.color.holo_green_dark
    } else {
        android.R.color.holo_red_dark
    }
    return ContextCompat.getColor(context, color)
}

@BindingAdapter("numberAsText")
fun bindNumberAsText(textView: TextView, text: Int) {
    textView.text = text.toString()
}

@BindingAdapter("onOptionClickListener")
fun bindOnOptionClickListener(textView: TextView, clickListener: OnOptionClickListener) {
    textView.setOnClickListener {
        clickListener.onOptionClick(textView.text.toString().toInt())
    }
}

interface OnOptionClickListener {
    fun onOptionClick(option: Int)
}
