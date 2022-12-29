package de.erikspall.mensaapp.ui.screens.foodproviders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.erikspall.mensaapp.R
import de.erikspall.mensaapp.domain.enums.Category
import de.erikspall.mensaapp.ui.components.FoodProvidersList
import de.erikspall.mensaapp.ui.MensaViewModel
import de.erikspall.mensaapp.ui.theme.Shrikhand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodProvidersScreen(
    modifier: Modifier = Modifier,
    onFoodProviderClick: (String) -> Unit = {},
    foodProviderCategory: Category,
    mensaViewModel: MensaViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current

    /*
     TODO: This is called multiple times :(
     Better: Combine FoodProviderViewModel and SettingsViewModel and read location directly, instead
     of checking if it has changed
    */
    //mensaViewModel.onEvent(FoodProviderScreenEvent.Init)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = when (foodProviderCategory) {
                            Category.CANTEEN -> stringResource(id = R.string.text_canteens)
                            Category.CAFETERIA -> stringResource(id = R.string.text_cafes)
                            else -> stringResource(id = R.string.text_invalid)
                        }, fontFamily = Shrikhand, style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->

            if (mensaViewModel.foodProviders.isEmpty()) {
                 // TODO: Show Lottie
            } else {
                FoodProvidersList(
                    modifier = modifier.padding(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        top = innerPadding.calculateTopPadding()
                    ),
                    list = when (foodProviderCategory) {
                        Category.CANTEEN -> mensaViewModel.canteens
                        Category.CAFETERIA -> mensaViewModel.cafeterias
                        else -> mensaViewModel.foodProviders
                    }
                )
            }
        }
    )


}