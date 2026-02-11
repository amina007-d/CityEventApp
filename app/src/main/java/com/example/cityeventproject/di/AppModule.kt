package com.example.cityeventproject.di

import android.content.Context
import androidx.room.Room
import com.example.cityeventproject.data.local.AppDatabase
import com.example.cityeventproject.data.local.dao.EventDao
import com.example.cityeventproject.data.remote.tm.TmApi
import com.example.cityeventproject.data.repo.AuthRepositoryImpl
import com.example.cityeventproject.data.repo.CommentsRepositoryImpl
import com.example.cityeventproject.data.repo.EventRepositoryImpl
import com.example.cityeventproject.data.repo.NotesRepositoryImpl
import com.example.cityeventproject.domain.repo.AuthRepository
import com.example.cityeventproject.domain.repo.CommentsRepository
import com.example.cityeventproject.domain.repo.EventRepository
import com.example.cityeventproject.domain.repo.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Providers {

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder()
            .addInterceptor(log)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://app.ticketmaster.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideTmApi(retrofit: Retrofit): TmApi = retrofit.create(TmApi::class.java)

    @Provides @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "cityevents.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideDbRef(): DatabaseReference = FirebaseDatabase.getInstance().reference
}

@Module
@InstallIn(SingletonComponent::class)
abstract class Binders {
    @Binds abstract fun bindAuthRepo(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun bindEventRepo(impl: EventRepositoryImpl): EventRepository
    @Binds abstract fun bindCommentsRepo(impl: CommentsRepositoryImpl): CommentsRepository
    @Binds abstract fun bindNotesRepo(impl: NotesRepositoryImpl): NotesRepository
}
