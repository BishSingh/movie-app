package com.android.bish.movieapp

import android.app.Activity
import android.app.Application
import com.android.bish.movieapp.dagger.ContextModule
import com.android.bish.movieapp.dagger.DaggerMovieApiComponent
import com.android.bish.movieapp.dagger.MovieApiComponent
import timber.log.Timber

class MovieApplication : Application() {

    lateinit var movieApplicationComponent: MovieApiComponent

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        movieApplicationComponent = DaggerMovieApiComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }

    companion object {
        operator fun get(activity: Activity): MovieApplication {
            return activity.application as MovieApplication
        }
    }

}