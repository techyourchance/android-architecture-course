package com.techyourchance.architecture.common.di

import android.app.Application
import androidx.room.Room
import com.techyourchance.architecture.BuildConfig
import com.techyourchance.architecture.common.Constants
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.database.MyRoomDatabase
import com.techyourchance.architecture.networking.StackoverflowApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            build()
        }

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    fun stackOverflowApi(retrofit: Retrofit): StackoverflowApi {
        return retrofit.create(StackoverflowApi::class.java)
    }

    @Provides
    @Singleton
    fun myRoomDatabase(application: Application): MyRoomDatabase {
        return Room.databaseBuilder(
            application,
            MyRoomDatabase::class.java,
            Constants.DB_NAME
        ).build()
    }

    @Provides
    fun favoriteQuestionDao(database: MyRoomDatabase): FavoriteQuestionDao {
        return database.favoriteQuestionDao
    }
}