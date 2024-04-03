package com.techyourchance.architecture

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {

    private val retrofit by lazy {
        val httpClient = OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            build()
        }

        Retrofit.Builder()
            .baseUrl("http://api.stackexchange.com/2.3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
    }

    private val stackoverflowApi by lazy {
        retrofit.create(StackoverflowApi::class.java)
    }

    private val myRoomDatabase by lazy {
        Room.databaseBuilder(
            this@MainActivity,
            MyRoomDatabase::class.java,
            "MyDatabase"
        ).build()
    }

    private val favoriteQuestionDao by lazy {
        myRoomDatabase.favoriteQuestionDao
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {

                val parentNavController = rememberNavController()

                val currentNavController = remember {
                    mutableStateOf(parentNavController)
                }

                Scaffold(
                    topBar = {
                        MyTopAppBar(
                            favoriteQuestionDao = favoriteQuestionDao,
                            currentNavController = currentNavController.value,
                            parentNavController = parentNavController,
                        )
                    },
                    bottomBar = {
                        BottomAppBar(modifier = Modifier) {
                            MyBottomTabsBar(parentController = parentNavController)
                        }
                    },
                    content = { padding ->
                        MyContent(
                            padding = padding,
                            parentNavController = parentNavController,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            currentNavController = currentNavController,
                        )
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

sealed class Route(val routeName: String, val bottomTab: BottomTab) {
    data object MainTab: Route("mainTab", BottomTab.Main)
    data object FavoritesTab: Route("favoritesTab", BottomTab.Favorites)
    data object QuestionsListScreen: Route("questionsList", BottomTab.Main)
    data object QuestionDetailsScreen: Route("questionDetails/{questionId}/{questionTitle}", BottomTab.Main)
    data object FavoriteQuestionsScreen: Route("favorites", BottomTab.Favorites)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    favoriteQuestionDao: FavoriteQuestionDao,
    currentNavController: NavHostController,
    parentNavController: NavHostController,
) {
    val scope = rememberCoroutineScope()

    val backstackEntryState = currentNavController.currentBackStackEntryAsState()

    val isRootRoute = remember(backstackEntryState.value) {
        backstackEntryState.value?.destination?.route == Route.QuestionsListScreen.routeName
    }

    val isShowFavoriteButton = remember(backstackEntryState.value) {
        backstackEntryState.value?.destination?.route == Route.QuestionDetailsScreen.routeName
    }

    val questionIdAndTitle = remember(backstackEntryState.value) {
        if (isShowFavoriteButton) {
            Pair(
                backstackEntryState.value?.arguments?.getString("questionId")!!,
                backstackEntryState.value?.arguments?.getString("questionTitle")!!,
            )
        } else {
            Pair("", "")
        }
    }

    var isFavoriteQuestion by remember { mutableStateOf(false) }

    if (isShowFavoriteButton && questionIdAndTitle.first.isNotEmpty()) {
        // Since collectAsState can't be conditionally called, use LaunchedEffect for conditional logic
        LaunchedEffect(questionIdAndTitle) {
            favoriteQuestionDao.observeById(questionIdAndTitle.first).collect { favoriteQuestion ->
                isFavoriteQuestion = favoriteQuestion != null
            }
        }
    }

    CenterAlignedTopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White
                )
            }
        },

        navigationIcon = {
            if (!isRootRoute) {
                IconButton(
                    onClick = {
                        if (!currentNavController.popBackStack()) {
                            parentNavController.popBackStack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "menu items"
                    )
                }
            }
        },

        actions = {
            if (isShowFavoriteButton) {
                IconButton(
                    onClick = {
                        scope.launch {
                            if (isFavoriteQuestion) {
                                favoriteQuestionDao.delete(questionIdAndTitle.first)
                            } else {
                                favoriteQuestionDao.upsert(FavoriteQuestion(questionIdAndTitle.first, questionIdAndTitle.second))
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavoriteQuestion) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
    )
}

sealed class BottomTab(val icon: ImageVector?, var title: String) {
    data object Main : BottomTab(Icons.Rounded.Home, "Home")
    data object Favorites : BottomTab(Icons.Rounded.Favorite, "Favorites")
}

@Composable
fun MyBottomTabsBar(parentController: NavController) {

    val bottomTabsToRootRoutes = remember() {
        mapOf(
            BottomTab.Main to Route.MainTab,
            BottomTab.Favorites to Route.FavoritesTab,
        )
    }

    val navBackStackEntry by parentController.currentBackStackEntryAsState()

    val currentRoute = remember(navBackStackEntry) {
        when(val currentRouteName = navBackStackEntry?.destination?.route) {
            Route.QuestionsListScreen.routeName -> Route.QuestionsListScreen
            Route.QuestionDetailsScreen.routeName -> Route.QuestionDetailsScreen
            Route.FavoriteQuestionsScreen.routeName -> Route.FavoriteQuestionsScreen
            Route.MainTab.routeName -> Route.MainTab
            Route.FavoritesTab.routeName -> Route.FavoritesTab
            null -> null
            else -> throw RuntimeException("unsupported route: $currentRouteName")
        }
    }

    NavigationBar {
        bottomTabsToRootRoutes.keys.forEachIndexed { _, bottomTab ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(bottomTab.icon!!, contentDescription = bottomTab.title) },
                label = { Text(bottomTab.title) },
                selected = currentRoute?.bottomTab == bottomTab,
                onClick = {
                    parentController.navigate(bottomTabsToRootRoutes[bottomTab]!!.routeName) {
                        parentController.graph.startDestinationRoute?.let { startRoute ->
                            popUpTo(startRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun MyContent(
    padding: PaddingValues,
    parentNavController: NavHostController,
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
    currentNavController: MutableState<NavHostController>,
) {
    Surface(
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = 12.dp),
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = parentNavController,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            startDestination = Route.MainTab.routeName,
        ) {
            composable(route = Route.MainTab.routeName) {
                val nestedNavController = rememberNavController()
                currentNavController.value = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.QuestionsListScreen.routeName) {
                    composable(route = Route.QuestionsListScreen.routeName) {
                        QuestionsListScreen(
                            stackoverflowApi = stackoverflowApi,
                            onQuestionClicked = { clickedQuestionId, clickedQuestionTitle ->
                                nestedNavController.navigate(
                                    Route.QuestionDetailsScreen.routeName
                                        .replace("{questionId}", clickedQuestionId)
                                        .replace("{questionTitle}", clickedQuestionTitle)
                                )
                            },
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen.routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            navController = nestedNavController,
                        )
                    }
                }

            }

            composable(route = Route.FavoritesTab.routeName) {
                val nestedNavController = rememberNavController()
                currentNavController.value = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.FavoriteQuestionsScreen.routeName) {
                    composable(route = Route.FavoriteQuestionsScreen.routeName) {
                        FavoriteQuestionsScreen(
                            favoriteQuestionDao = favoriteQuestionDao,
                            navController = nestedNavController
                        )
                    }
                    composable(route = Route.QuestionDetailsScreen.routeName) { backStackEntry ->
                        QuestionDetailsScreen(
                            questionId = backStackEntry.arguments?.getString("questionId")!!,
                            stackoverflowApi = stackoverflowApi,
                            favoriteQuestionDao = favoriteQuestionDao,
                            navController = nestedNavController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteQuestionsScreen(
    favoriteQuestionDao: FavoriteQuestionDao,
    navController: NavHostController,
) {
    val favorites = favoriteQuestionDao.observe().collectAsState(initial = listOf())

    if (favorites.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
        ) {
            items(favorites.value.size) { index ->
                val favoriteQuestion = favorites.value[index]
                QuestionItem(
                    questionId = favoriteQuestion.id,
                    questionTitle = favoriteQuestion.title,
                    onQuestionClicked = {
                        navController.navigate(
                            Route.QuestionDetailsScreen.routeName
                                .replace("{questionId}", favoriteQuestion.id)
                                .replace("{questionTitle}", favoriteQuestion.title)
                        )
                    },
                )
                if (index < favorites.value.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 20.dp),
                        thickness = 2.dp
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "No favorites",
            )
        }

    }

}

@Composable
fun QuestionsListScreen(
    stackoverflowApi: StackoverflowApi,
    onQuestionClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var questions by remember { mutableStateOf<List<QuestionSchema>>(listOf()) }

    LaunchedEffect(Unit) {
        questions = stackoverflowApi.fetchLastActiveQuestions(20)!!.questions
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
    ) {
        items(questions.size) { index ->
            val question = questions[index]
            QuestionItem(
                questionId = question.id,
                questionTitle = question.title,
                onQuestionClicked = { onQuestionClicked(question.id, question.title) },
            )
            if (index < questions.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    thickness = 2.dp
                )
            }
        }
    }
}

@Composable
fun QuestionItem(
    questionId: String,
    questionTitle: String,
    onQuestionClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {
                onQuestionClicked()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            val spannedTitle: Spanned = Html.fromHtml(questionTitle, Html.FROM_HTML_MODE_LEGACY)
            Text(
                modifier = Modifier
                    .weight(1.8f),
                text = spannedTitle.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun QuestionDetailsScreen(
    questionId: String,
    stackoverflowApi: StackoverflowApi,
    favoriteQuestionDao: FavoriteQuestionDao,
    navController: NavHostController,
) {
    var questionDetails by remember { mutableStateOf<QuestionWithBodySchema?>(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(questionId) {
        try {
            questionDetails = stackoverflowApi.fetchQuestionDetails(questionId)!!.questions[0]
        } catch (e: Exception) {
            isError = true
        }
    }

    val isFavorite by favoriteQuestionDao.observeById(questionId).collectAsState(initial = null)

    val scrollState = rememberScrollState()

    if (questionDetails != null) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            questionDetails?.let {

                Spacer(modifier = Modifier.height(20.dp))

                val spannedTitle: Spanned = Html.fromHtml(it.title, Html.FROM_HTML_MODE_LEGACY)
                Text(
                    text = spannedTitle.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                val spannedBody: Spanned = Html.fromHtml(it.body, Html.FROM_HTML_MODE_LEGACY)
                Text(
                    text = spannedBody.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (isError) {
        AlertDialog(
            text = {
                Text("Ooops, something went wrong")
            },
            onDismissRequest = {
                navController.popBackStack()
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}