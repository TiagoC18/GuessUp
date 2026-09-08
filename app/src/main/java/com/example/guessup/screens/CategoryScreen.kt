package com.example.guessup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessup.model.Category
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun CategoryScreen(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    onEditCategory: (Category) -> Unit,
    onAddCategory: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {

        /*
         * HEADER
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Categorias",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Escolhe o teu desafio",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            /*
             * Botão Nova categoria
             */
            Button(
                onClick = onAddCategory,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(
                    horizontal = 18.dp,
                    vertical = 12.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary
                )
            ) {

                Text(
                    text = "+  Nova",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*
         * GRID
         */
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 24.dp
            ),
            horizontalArrangement =
                Arrangement.spacedBy(14.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            itemsIndexed(
                items = categories,
                key = { _, category ->
                    category.id
                }
            ) { index, category ->

                AnimatedCategoryCard(
                    category = category,
                    index = index,

                    onClick = {
                        onCategoryClick(category)
                    },

                    onEditClick = {
                        onEditCategory(category)
                    }
                )
            }
        }
    }
}

@Composable
private fun AnimatedCategoryCard(
    category: Category,
    index: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        /*
         * Pequeno atraso entre cartões.
         *
         * Limitamos o atraso para listas grandes
         * não demorarem demasiado.
         */
        delay(
            (index.coerceAtMost(6) * 45L)
        )

        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter =
            fadeIn(
                animationSpec = tween(280)
            ) +
                    scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(280)
                    )
    ) {

        CategoryCard(
            category = category,
            onClick = onClick,
            onEditClick = onEditClick
        )
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            /*
             * Conteúdo principal
             */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                verticalArrangement =
                    Arrangement.SpaceBetween
            ) {

                /*
                 * Emoji dentro de um círculo
                 */
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    color =
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.12f
                        )
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = category.emoji,
                            fontSize = 30.sp
                        )
                    }
                }

                Column {

                    Text(
                        text = category.name,
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text =
                            "${category.words.size} palavras",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant,
                        modifier =
                            Modifier.padding(top = 3.dp)
                    )
                }
            }

            /*
             * Editar
             */
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape),
                shape = CircleShape,
                color =
                    MaterialTheme.colorScheme.surfaceVariant
            ) {

                TextButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(46.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {

                    Text(
                        text = "✎",
                        fontSize = 22.sp,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}