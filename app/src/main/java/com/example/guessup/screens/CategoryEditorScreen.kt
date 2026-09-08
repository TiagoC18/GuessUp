package com.example.guessup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessup.data.parseBulkWords
import com.example.guessup.model.Category
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun CategoryEditorScreen(
    category: Category?,
    onSave: (
        name: String,
        emoji: String,
        words: List<String>
    ) -> Unit,
    onCancel: () -> Unit
) {

    val isEditing =
        category != null

    var name by remember(category?.id) {
        mutableStateOf(
            category?.name ?: ""
        )
    }

    var emoji by remember(category?.id) {
        mutableStateOf(
            category?.emoji ?: "🎯"
        )
    }

    var newWordsText by remember {
        mutableStateOf("")
    }

    val words =
        remember(category?.id) {

            mutableStateListOf<String>().apply {

                category?.words?.let {
                    addAll(it)
                }
            }
        }

    fun addNewWords() {

        val parsed =
            parseBulkWords(newWordsText)

        parsed.forEach { word ->

            val alreadyExists =
                words.any {
                    it.equals(
                        word,
                        ignoreCase = true
                    )
                }

            if (!alreadyExists) {
                words.add(word)
            }
        }

        newWordsText = ""
    }

    fun saveCategory() {

        if (newWordsText.isNotBlank()) {
            addNewWords()
        }

        if (
            name.isNotBlank() &&
            words.isNotEmpty()
        ) {

            onSave(
                name.trim(),
                emoji.trim(),
                words.toList()
            )
        }
    }

    val backgroundGradient =
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .safeDrawingPadding()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * HEADER
             */
            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 12.dp
                )
            ) {

                Text(
                    text = if (isEditing) {
                        "Editar categoria"
                    } else {
                        "Nova categoria"
                    },
                    style =
                        MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isEditing) {
                        "Personaliza as palavras desta categoria"
                    } else {
                        "Cria o teu próprio desafio"
                    },
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            /*
             * CONTEÚDO COM SCROLL
             */
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                item {

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Informação",
                                style =
                                    MaterialTheme.typography
                                        .titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                },
                                label = {
                                    Text("Nome da categoria")
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            )

                            OutlinedTextField(
                                value = emoji,
                                onValueChange = {
                                    emoji = it
                                },
                                label = {
                                    Text("Emoji")
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            )
                        }
                    }
                }

                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 12.dp,
                                bottom = 2.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Palavras",
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color =
                                MaterialTheme.colorScheme.primary
                                    .copy(alpha = 0.12f)
                        ) {

                            Text(
                                text = "${words.size}",
                                fontWeight = FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    horizontal = 13.dp,
                                    vertical = 6.dp
                                )
                            )
                        }
                    }
                }

                /*
                 * PALAVRAS EXISTENTES
                 */
                items(
                    items = words,
                    key = { word ->
                        word
                    }
                ) { word ->

                    RemovableWordCard(
                        word = word,
                        onRemove = {

                            words.remove(word)
                        }
                    )
                }

                /*
                 * ADICIONAR PALAVRAS
                 */
                item {

                    Card(
                        modifier = Modifier.padding(top = 14.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Adicionar palavras",
                                style =
                                    MaterialTheme.typography
                                        .titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Separa os nomes por Enter, vírgula ou ponto e vírgula.",
                                style =
                                    MaterialTheme.typography.bodySmall,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                modifier =
                                    Modifier.padding(top = 3.dp)
                            )

                            OutlinedTextField(
                                value = newWordsText,
                                onValueChange = {
                                    newWordsText = it
                                },
                                placeholder = {
                                    Text(
                                        "Cristiano Ronaldo\n" +
                                                "Lionel Messi, Mbappé; Haaland"
                                    )
                                },
                                minLines = 4,
                                maxLines = 7,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            )

                            Button(
                                onClick = {
                                    addNewWords()
                                },
                                enabled =
                                    newWordsText.isNotBlank(),
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 10.dp),
                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text = "+ Adicionar",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            /*
             * BOTÕES FIXOS EM BAIXO
             */
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            saveCategory()
                        },
                        enabled =
                            name.isNotBlank() &&
                                    (
                                            words.isNotEmpty() ||
                                                    newWordsText.isNotBlank()
                                            ),
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Text(
                            text = if (isEditing) {
                                "Guardar"
                            } else {
                                "Criar"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RemovableWordCard(
    word: String,
    onRemove: () -> Unit
) {

    var visible by remember {
        mutableStateOf(true)
    }

    /*
     * Quando visible passa para false:
     *
     * primeiro executamos a animação
     * e só depois removemos realmente
     * da lista.
     */
    LaunchedEffect(visible) {

        if (!visible) {

            delay(220)

            onRemove()
        }
    }

    AnimatedVisibility(
        visible = visible,
        exit =
            fadeOut(
                animationSpec = tween(180)
            ) +
                    shrinkVertically(
                        animationSpec = tween(220)
                    )
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 6.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = word,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    modifier =
                        Modifier.weight(1f)
                )

                TextButton(
                    onClick = {

                        visible = false
                    }
                ) {

                    Text(
                        text = "×",
                        fontSize = 26.sp,
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}