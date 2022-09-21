package de.erikspall.mensaapp.domain.usecases.foodprovider

data class FoodProviderUseCases (
    val getFoodProvidersWithoutMenu: GetFoodProvidersWithoutMenu,
    val getOpeningHoursAsString: GetOpeningHoursAsString,
    val getInfoOfFoodProvider: GetInfoOfFoodProvider
)