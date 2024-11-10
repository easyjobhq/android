package com.easyjob.jetpack.di

import android.content.Context
import com.easyjob.jetpack.data.store.UserPreferencesRepository
import com.easyjob.jetpack.repositories.AuthRepository
import com.easyjob.jetpack.repositories.AuthRepositoryImpl
import com.easyjob.jetpack.repositories.ReviewRepository
import com.easyjob.jetpack.repositories.ReviewRepositoryImpl
import com.easyjob.jetpack.repositories.SearchScreenRepository
import com.easyjob.jetpack.repositories.SearchScreenRepositoryImpl
import com.easyjob.jetpack.services.AuthService
import com.easyjob.jetpack.services.AuthServiceImpl
import com.easyjob.jetpack.services.ReviewService
import com.easyjob.jetpack.services.ReviewServiceImpl
import com.easyjob.jetpack.services.SearchScreenService
import com.easyjob.jetpack.services.SearchScreenServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context // Necesitas `@ApplicationContext` aquí si es inyectado
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    //Services

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return AuthServiceImpl()
    }

    @Provides
    @Singleton
    fun provideAuthSearchService(): SearchScreenService {
        return SearchScreenServiceImpl()
    }

    @Provides
    @Singleton
    fun provideReviewService(): ReviewService {
        return ReviewServiceImpl()
    }

    //Repositories

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        userPreferencesRepository: UserPreferencesRepository
    ): AuthRepository {
        return AuthRepositoryImpl(authService, userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideSearchScreenRepository(
        searchScreenService: SearchScreenService
    ): SearchScreenRepository {
        return SearchScreenRepositoryImpl(searchScreenService)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(
        reviewService: ReviewService
    ): ReviewRepository {
        return ReviewRepositoryImpl(reviewService)
    }
}