package com.example.guessup.data

fun parseBulkWords(
    text: String
): List<String> {

    return text
        .split(
            Regex("[,;\\r\\n]+")
        )
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
}