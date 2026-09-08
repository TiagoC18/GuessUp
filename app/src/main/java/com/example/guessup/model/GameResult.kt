package com.example.guessup.model

data class CardResult(
    val word: String,
    val isCorrect: Boolean
)

data class RoundResult(
    val categoryId: String,
    val categoryName: String,
    val durationSeconds: Int,
    val cards: List<CardResult>
) {
    val correctCount: Int
        get() = cards.count { it.isCorrect }

    val skippedCount: Int
        get() = cards.count { !it.isCorrect }
}