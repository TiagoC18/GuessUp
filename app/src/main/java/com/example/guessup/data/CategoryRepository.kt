package com.example.guessup.data

import android.content.Context
import com.example.guessup.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object CategoryRepository {

    private const val PREFS_NAME = "guessup_categories"
    private const val KEY_CATEGORIES = "categories"

    private var initialized = false

    private lateinit var context: Context

    private val defaultCategories = listOf(

        Category(
            id = "football",
            name = "Futebol",
            emoji = "⚽",
            words = listOf(
                "Cristiano Ronaldo",
                "Lionel Messi",
                "José Mourinho",
                "Real Madrid",
                "Barcelona",
                "Sporting",
                "Benfica",
                "Porto",
                "Champions League",
                "Mundial",
                "Penálti",
                "Fora de jogo",
                "Guarda-redes",
                "Árbitro",
                "Estádio"
            )
        ),

        Category(
            id = "movies",
            name = "Filmes",
            emoji = "🎬",
            words = listOf(
                "Titanic",
                "Avatar",
                "Gladiador",
                "Shrek",
                "Harry Potter",
                "Star Wars",
                "Jurassic Park",
                "Toy Story",
                "O Rei Leão",
                "Batman",
                "Joker",
                "Matrix",
                "Rocky",
                "Frozen",
                "Indiana Jones"
            )
        ),

        Category(
            id = "animals",
            name = "Animais",
            emoji = "🐶",
            words = listOf(
                "Elefante",
                "Girafa",
                "Leão",
                "Tigre",
                "Cão",
                "Gato",
                "Macaco",
                "Crocodilo",
                "Tubarão",
                "Golfinho",
                "Pinguim",
                "Canguru",
                "Cavalo",
                "Coelho",
                "Urso"
            )
        ),

        Category(
            id = "countries",
            name = "Países",
            emoji = "🌍",
            words = listOf(
                "Portugal",
                "Espanha",
                "França",
                "Alemanha",
                "Itália",
                "Brasil",
                "Argentina",
                "Japão",
                "China",
                "Austrália",
                "Canadá",
                "México",
                "Suécia",
                "Grécia",
                "Egito"
            )
        ),

        Category(
            id = "games",
            name = "Videojogos",
            emoji = "🎮",
            words = listOf(
                "Minecraft",
                "Fortnite",
                "GTA",
                "FIFA",
                "Call of Duty",
                "Mario",
                "Pokémon",
                "The Last of Us",
                "God of War",
                "Red Dead Redemption",
                "Zelda",
                "Sonic",
                "Cyberpunk",
                "Resident Evil",
                "Need for Speed"
            )
        ),

        Category(
            id = "food",
            name = "Comida",
            emoji = "🍕",
            words = listOf(
                "Pizza",
                "Hambúrguer",
                "Sushi",
                "Lasanha",
                "Bacalhau",
                "Francesinha",
                "Pastel de nata",
                "Gelado",
                "Chocolate",
                "Arroz",
                "Massa",
                "Bife",
                "Salada",
                "Panquecas",
                "Kebab"
            )
        )
    )

    private val _categories =
        MutableStateFlow(defaultCategories)

    val categories: StateFlow<List<Category>> =
        _categories.asStateFlow()

    fun init(context: Context) {

        if (initialized) {
            return
        }

        this.context = context.applicationContext
        initialized = true

        val preferences =
            this.context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val savedJson =
            preferences.getString(
                KEY_CATEGORIES,
                null
            )

        if (savedJson == null) {

            _categories.value = defaultCategories
            save()

        } else {

            _categories.value =
                runCatching {
                    decodeCategories(savedJson)
                }.getOrElse {
                    defaultCategories
                }
        }
    }

    fun getCategory(
        id: String
    ): Category? {

        return _categories.value.find {
            it.id == id
        }
    }

    fun addCategory(
        name: String,
        emoji: String,
        words: List<String>
    ) {

        val category = Category(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            emoji = emoji.trim().ifBlank { "🎯" },
            words = cleanWords(words)
        )

        _categories.value =
            _categories.value + category

        save()
    }

    fun updateCategory(
        id: String,
        name: String,
        emoji: String,
        words: List<String>
    ) {

        _categories.value =
            _categories.value.map { category ->

                if (category.id == id) {

                    category.copy(
                        name = name.trim(),
                        emoji = emoji.trim()
                            .ifBlank { "🎯" },
                        words = cleanWords(words)
                    )

                } else {

                    category
                }
            }

        save()
    }

    private fun cleanWords(
        words: List<String>
    ): List<String> {

        return words
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
    }

    private fun save() {

        if (!initialized) {
            return
        }

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putString(
                KEY_CATEGORIES,
                encodeCategories(
                    _categories.value
                )
            )
            .apply()
    }

    private fun encodeCategories(
        categories: List<Category>
    ): String {

        val categoryArray = JSONArray()

        categories.forEach { category ->

            val wordsArray = JSONArray()

            category.words.forEach { word ->
                wordsArray.put(word)
            }

            val categoryObject =
                JSONObject().apply {

                    put("id", category.id)
                    put("name", category.name)
                    put("emoji", category.emoji)
                    put("words", wordsArray)
                }

            categoryArray.put(categoryObject)
        }

        return categoryArray.toString()
    }

    private fun decodeCategories(
        json: String
    ): List<Category> {

        val categoryArray =
            JSONArray(json)

        return List(categoryArray.length()) { index ->

            val categoryObject =
                categoryArray.getJSONObject(index)

            val wordsArray =
                categoryObject.getJSONArray("words")

            val words =
                List(wordsArray.length()) { wordIndex ->

                    wordsArray.getString(wordIndex)
                }

            Category(
                id = categoryObject.getString("id"),
                name = categoryObject.getString("name"),
                emoji = categoryObject.getString("emoji"),
                words = words
            )
        }
    }
}