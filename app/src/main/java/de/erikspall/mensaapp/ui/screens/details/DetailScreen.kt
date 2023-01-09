package de.erikspall.mensaapp.ui.screens.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import de.erikspall.mensaapp.R
import de.erikspall.mensaapp.domain.model.FoodProvider
import de.erikspall.mensaapp.domain.model.Meal
import de.erikspall.mensaapp.ui.MensaViewModel
import de.erikspall.mensaapp.ui.components.DetailHeader
import de.erikspall.mensaapp.ui.components.ExpandableTextState
import de.erikspall.mensaapp.ui.components.FancyIndicator
import de.erikspall.mensaapp.ui.components.MealCard
import de.erikspall.mensaapp.ui.theme.Shrikhand
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.absoluteValue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    foodProvider: FoodProvider,
    chipScrollState: ScrollState = rememberScrollState(),
    descriptionState: ExpandableTextState = remember {
        ExpandableTextState()
    },
    additionalInfoState: ExpandableTextState = remember {
        ExpandableTextState()
    },
    onBackClicked: (() -> Unit)? = null,
    mensaViewModel: MensaViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var hideBackButton by remember { mutableStateOf(false) }

    val hideBackButtonState = remember { MutableTransitionState(!hideBackButton) }

    val pagerState = rememberPagerState()

    //TODO Extract to own component
    var currentPageIndex by remember {
        mutableStateOf(0)
    }
    val scope = rememberCoroutineScope()

    // TODO: Merge this
    val pages = (0..13).toList()
    val menuMap = mutableMapOf<Int, SnapshotStateList<Meal>>()
    pages.forEach { offset ->
        menuMap[offset] = remember {
            mutableStateListOf()
        }
    }


    with(pagerState) {
        LaunchedEffect(key1 = currentPageIndex) {
            launch {
                animateScrollToPage(
                    page = (currentPageIndex)
                )
            }
        }
    }


    // Reuse the default offset animation modifier, but use our own indicator
    /* val indicator = @Composable { tabPositions ->
        FancyIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).padding(horizontal = 20.dp)
        )
    }*/


    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // Smoother
                if (delta.absoluteValue > 1.0) {
                    hideBackButton = delta < 0
                    hideBackButtonState.targetState = !hideBackButton
                }

                // called when you scroll the content
                return Offset.Zero
            }
        }
    }


    BackHandler(enabled = onBackClicked != null) {
        if (onBackClicked != null) {
            onBackClicked()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = foodProvider.name,
                        fontFamily = Shrikhand,
                        //style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (onBackClicked != null) {
                                onBackClicked()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                  IconToggleButton(
                      checked = true,
                      onCheckedChange = {}
                  ) {
                      Icon(
                          imageVector = Icons.Rounded.Favorite,
                          contentDescription = "",
                          tint = Color.Red
                      )
                  }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DetailHeader(
                    modifier = Modifier.padding(top = 8.dp),
                    foodProvider = foodProvider,
                    chipScrollState = chipScrollState,
                    descriptionState = descriptionState,
                    additionalInfoState = additionalInfoState
                )
            }
            // Tabs
            stickyHeader {
                Column(
                    //  modifier = Modifier.padding(top = 16.dp)
                ) {
                    // TODO: Check if pager support m3 tabrow
                    androidx.compose.material.ScrollableTabRow(
                        edgePadding = 20.dp,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            FancyIndicator(
                                Modifier
                                    .pagerTabIndicatorOffset(pagerState, tabPositions)
                            )

                        }
                    ) {
                        // Add tabs for all of our pages
                        pages.forEach { dayOffset ->
                            val date = LocalDate.now().plusDays(dayOffset.toLong())
                            Tab(
                                modifier = Modifier.wrapContentWidth(),
                                text = {
                                    val dayOfWeek = date.dayOfWeek.getDisplayName(
                                        TextStyle.SHORT_STANDALONE,
                                        Locale.getDefault()
                                    )
                                    val dateFormatted =
                                        date.format(
                                            DateTimeFormatter.ofLocalizedDate(
                                                FormatStyle.SHORT
                                            )
                                        )

                                    Text("$dayOfWeek\n$dateFormatted")
                                },
                                selected = pagerState.currentPage == dayOffset,
                                onClick = {
                                    Log.d("TABS", "new index: $dayOffset")
                                    currentPageIndex = dayOffset
                                }
                            )
                        }
                    }
                }
            }
            item {
                HorizontalPager(
                    count = pages.size,
                    state = pagerState,
                ) { page ->
                    LaunchedEffect(key1 = "$currentPageIndex" + "menus") {
                        /*scope.*/launch {

                        val menu = mensaViewModel.getMenu(
                            foodProvider.id!!,
                            page
                        )
                        val meals = if (menu.isSuccess) {
                            menu.getOrThrow().meals
                        } else {
                            emptyList()
                        }

                        menuMap[page]!!.clear()
                        menuMap[page]!!.addAll(meals)

                    }
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        // contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        menuMap[page]?.forEach { meal ->
                            MealCard(meal = meal)
                        }
                        Spacer(modifier = Modifier.height(2000.dp))
                    }

                }
                /*Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Text tab ${selectedTab + 1} selected",
                    style = MaterialTheme.typography.bodyLarge
                )*/
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewDetailScreen() {


    DetailScreen(
        foodProvider = FoodProvider(
            name = "Campus Hubland Nord",
            photo = R.drawable.mensateria_campus_hubland_nord_wuerzburg,
            openingHoursString = "Öffnet in 5 min",
            location = "Würzburg",
            type = "Mensateria",
            additionalInfo = "Die Abendmensa hat geschlossen!",
            description = "Die Mensateria mit 500 Innen- und 60 Balkonplätzen befindet sich im Obergeschoss des Gebäudes am westlichen Rand des Campus Nord am Hubland. Zur Südseite hin trägt die Mensateria einen Balkon, so dass ein Aufenthalt im Freien mit Blick auf den Hubland-Campus möglich ist.\n"
                    + "\nAusgestattet ist die Mensateria mit einem modernen Speiseleitsystem sowie speziellen Angebotsvarianten."
        )
    )
}