package com.example.guessup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guessup.data.CategoryRepository
import com.example.guessup.model.RoundResult
import com.example.guessup.screens.CategoryEditorScreen
import com.example.guessup.screens.CategoryScreen
import com.example.guessup.screens.DurationScreen
import com.example.guessup.screens.GameScreen
import com.example.guessup.screens.HomeScreen
import com.example.guessup.screens.ResultsScreen
import com.example.guessup.data.SettingsRepository
import com.example.guessup.screens.SettingsScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween

@Composable
fun GuessUpNavigation() {

    val navController = rememberNavController()

    /*
     * As categorias são agora um StateFlow.
     *
     * Isto faz com que o ecrã seja automaticamente
     * atualizado quando:
     *
     * - criamos uma categoria
     * - editamos uma categoria
     * - adicionamos palavras
     * - removemos palavras
     */
    val categories by
    CategoryRepository
        .categories
        .collectAsState()

    val settings by
    SettingsRepository
        .settings
        .collectAsState()

    /*
     * Último resultado da ronda.
     */
    var lastResult by remember {
        mutableStateOf<RoundResult?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "home",

        /*
         * Navegação para a frente:
         * o novo ecrã entra ligeiramente pela direita.
         */
        enterTransition = {

            fadeIn(
                animationSpec = tween(
                    durationMillis = 220
                )
            ) +

                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 280
                        ),
                        initialOffsetX = { fullWidth ->

                            fullWidth / 6
                        }
                    )
        },

        /*
         * O ecrã atual desaparece ligeiramente
         * para a esquerda.
         */
        exitTransition = {

            fadeOut(
                animationSpec = tween(
                    durationMillis = 170
                )
            ) +

                    slideOutHorizontally(
                        animationSpec = tween(
                            durationMillis = 230
                        ),
                        targetOffsetX = { fullWidth ->

                            -fullWidth / 10
                        }
                    )
        },

        /*
         * Quando carregamos em Back,
         * fazemos a animação inversa.
         */
        popEnterTransition = {

            fadeIn(
                animationSpec = tween(
                    durationMillis = 220
                )
            ) +

                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 280
                        ),
                        initialOffsetX = { fullWidth ->

                            -fullWidth / 6
                        }
                    )
        },

        popExitTransition = {

            fadeOut(
                animationSpec = tween(
                    durationMillis = 170
                )
            ) +

                    slideOutHorizontally(
                        animationSpec = tween(
                            durationMillis = 230
                        ),
                        targetOffsetX = { fullWidth ->

                            fullWidth / 10
                        }
                    )
        }
    ) {

        /*
         * HOME
         */
        composable("home") {

            HomeScreen(
                onPlayClick = {

                    navController.navigate(
                        "categories"
                    )
                },

                onSettingsClick = {

                    navController.navigate(
                        "settings"
                    )
                }
            )
        }

        composable("settings") {

            SettingsScreen(
                settings = settings,

                onVibrationChanged = {

                    SettingsRepository
                        .setVibrationEnabled(it)
                },

                onSoundChanged = {

                    SettingsRepository
                        .setSoundEnabled(it)
                },

                onCountdownChanged = {

                    SettingsRepository
                        .setCountdownSeconds(it)
                },

                onThemeChanged = {

                    SettingsRepository
                        .setThemeMode(it)
                }
            )
        }

        /*
         * CATEGORIAS
         */
        composable("categories") {

            CategoryScreen(
                categories = categories,

                /*
                 * Carregar normalmente numa categoria:
                 *
                 * Futebol
                 *    ↓
                 * escolher duração
                 */
                onCategoryClick = { category ->

                    navController.navigate(
                        "duration/${category.id}"
                    )
                },

                /*
                 * EDITAR categoria
                 */
                onEditCategory = { category ->

                    navController.navigate(
                        "categoryEditor/${category.id}"
                    )
                },

                /*
                 * CRIAR nova categoria
                 */
                onAddCategory = {

                    navController.navigate(
                        "categoryEditor/new"
                    )
                }
            )
        }

        /*
         * CRIAR / EDITAR CATEGORIA
         *
         * Exemplos:
         *
         * categoryEditor/new
         *
         * categoryEditor/football
         *
         * categoryEditor/<uuid>
         */
        composable(
            route = "categoryEditor/{categoryId}"
        ) { backStackEntry ->

            val categoryId =
                backStackEntry.arguments
                    ?.getString("categoryId")

            /*
             * Se for "new":
             *
             * category = null
             *
             * O CategoryEditorScreen percebe
             * que estamos a criar uma categoria.
             *
             * Caso contrário, procuramos
             * a categoria existente.
             */
            val category =
                if (categoryId == "new") {

                    null

                } else {

                    categoryId?.let { id ->

                        CategoryRepository.getCategory(
                            id
                        )
                    }
                }

            CategoryEditorScreen(
                category = category,

                /*
                 * GUARDAR
                 */
                onSave = { name, emoji, words ->

                    if (category == null) {

                        /*
                         * NOVA CATEGORIA
                         */
                        CategoryRepository.addCategory(
                            name = name,
                            emoji = emoji,
                            words = words
                        )

                    } else {

                        /*
                         * EDITAR CATEGORIA EXISTENTE
                         */
                        CategoryRepository.updateCategory(
                            id = category.id,
                            name = name,
                            emoji = emoji,
                            words = words
                        )
                    }

                    /*
                     * Depois de guardar:
                     *
                     * volta para Categorias.
                     */
                    navController.popBackStack()
                },

                /*
                 * CANCELAR
                 */
                onCancel = {

                    navController.popBackStack()
                }
            )
        }

        /*
         * ESCOLHER DURAÇÃO
         */
        composable(
            route = "duration/{categoryId}"
        ) { backStackEntry ->

            val categoryId =
                backStackEntry.arguments
                    ?.getString("categoryId")

            /*
             * Agora usamos getCategory(),
             * porque categories deixou de ser
             * uma List direta no Repository.
             */
            val category =
                categoryId?.let { id ->

                    CategoryRepository.getCategory(
                        id
                    )
                }

            if (category != null) {

                DurationScreen(
                    category = category,

                    onStartGame = { durationSeconds ->

                        /*
                         * Exemplo:
                         *
                         * game/football/60
                         */
                        navController.navigate(
                            "game/${category.id}/$durationSeconds"
                        )
                    }
                )
            }
        }

        /*
         * JOGO
         */
        composable(
            route = "game/{categoryId}/{durationSeconds}",

            enterTransition = {

                fadeIn(
                    animationSpec = tween(
                        durationMillis = 180
                    )
                )
            },

            exitTransition = {

                fadeOut(
                    animationSpec = tween(
                        durationMillis = 140
                    )
                )
            },

            popEnterTransition = {

                fadeIn(
                    animationSpec = tween(
                        durationMillis = 180
                    )
                )
            },

            popExitTransition = {

                fadeOut(
                    animationSpec = tween(
                        durationMillis = 140
                    )
                )
            }

        ) { backStackEntry ->

            val categoryId =
                backStackEntry.arguments
                    ?.getString("categoryId")

            val durationSeconds =
                backStackEntry.arguments
                    ?.getString("durationSeconds")
                    ?.toIntOrNull()
                    ?: 60

            val category =
                categoryId?.let { id ->

                    CategoryRepository.getCategory(
                        id
                    )
                }

            if (category != null) {

                GameScreen(
                    category = category,
                    durationSeconds = durationSeconds,

                    vibrationEnabled =
                        settings.vibrationEnabled,

                    soundEnabled =
                        settings.soundEnabled,

                    countdownSeconds =
                        settings.countdownSeconds,

                    onGameFinished = { result ->

                        lastResult = result

                        navController.navigate(
                            "results"
                        ) {

                            popUpTo(
                                "game/{categoryId}/{durationSeconds}"
                            ) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        /*
         * RESULTADOS
         */
        composable(
            route = "results",

            enterTransition = {

                fadeIn(
                    animationSpec = tween(
                        durationMillis = 200
                    )
                )
            },

            exitTransition = {

                fadeOut(
                    animationSpec = tween(
                        durationMillis = 160
                    )
                )
            },

            popEnterTransition = {

                fadeIn(
                    animationSpec = tween(
                        durationMillis = 200
                    )
                )
            },

            popExitTransition = {

                fadeOut(
                    animationSpec = tween(
                        durationMillis = 160
                    )
                )
            }

        ) {

            lastResult?.let { result ->

                ResultsScreen(
                    result = result,

                    /*
                     * JOGAR NOVAMENTE
                     *
                     * Mantém:
                     *
                     * - mesma categoria
                     * - mesma duração
                     */
                    onPlayAgain = {

                        lastResult = null

                        navController.navigate(
                            "game/${result.categoryId}/${result.durationSeconds}"
                        ) {

                            popUpTo(
                                "results"
                            ) {
                                inclusive = true
                            }
                        }
                    },

                    /*
                     * VOLTAR ÀS CATEGORIAS
                     */
                    onBackToCategories = {

                        lastResult = null

                        navController.popBackStack(
                            route = "categories",
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}