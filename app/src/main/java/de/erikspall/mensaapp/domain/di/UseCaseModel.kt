package de.erikspall.mensaapp.domain.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.erikspall.mensaapp.domain.interfaces.data.AppRepository
import de.erikspall.mensaapp.domain.usecases.foodproviders.FoodProviderUseCases
import de.erikspall.mensaapp.domain.usecases.foodproviders.FetchFoodProvider
import de.erikspall.mensaapp.domain.usecases.foodproviders.FetchFoodProviders
import de.erikspall.mensaapp.domain.usecases.foodproviders.FetchMenu
import de.erikspall.mensaapp.domain.usecases.additives.*
import de.erikspall.mensaapp.domain.usecases.openinghours.FormatToString
import de.erikspall.mensaapp.domain.usecases.openinghours.OpeningHourUseCases
import de.erikspall.mensaapp.domain.usecases.sharedpreferences.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModel {

    @Provides
    @Singleton
    fun provideFoodProviderUseCases(
        appRepository: AppRepository
    ): FoodProviderUseCases {
        return FoodProviderUseCases(
            fetchAll = FetchFoodProviders(appRepository),
            fetch = FetchFoodProvider(appRepository),
            fetchMenu = FetchMenu(appRepository)
        )
    }

    @Provides
    @Singleton
    fun provideOpeningHourUseCases(
        @ApplicationContext context: Context
    ): OpeningHourUseCases {
        return OpeningHourUseCases(
            formatToString = FormatToString(
                res = context.resources
            ),
        )
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceUseCases(
        @ApplicationContext appContext: Context,
        sharedPref: SharedPreferences
    ): SharedPreferenceUseCases {
        return SharedPreferenceUseCases(
            setValue = SetValue(sharedPref),
            getValue = GetValue(appContext, sharedPref),
            registerListener = RegisterListener(sharedPref),
            getValueRes = GetValueRes(sharedPref),
            setBoolean = SetBoolean(sharedPref),
            getBoolean = GetBoolean(sharedPref),
            setLocalDateTime = SetLocalDateTime(appContext, sharedPref),
            getLocalDateTime = GetLocalDateTime(appContext, sharedPref)
        )
    }

    @Provides
    @Singleton
    fun provideMealComponentUseCases(
        appRepository: AppRepository
    ): AdditiveUseCases {
        return AdditiveUseCases(
            setAdditiveLikeStatus = SetAdditiveLikeStatus(appRepository),
            getAdditives = GetAdditives(appRepository),
            fetchLatest = FetchLatest(appRepository)
        )
    }
}