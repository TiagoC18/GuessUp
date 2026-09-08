package com.example.guessup.model

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val words: List<String>
)