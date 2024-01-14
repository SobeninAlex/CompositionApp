package com.example.compositionapp.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * ENUM классы неявно реализуют интерфейс Serializable,
 */

@Parcelize
enum class Level : Parcelable {
    TEST, EASY, NORMAL, HARD
}