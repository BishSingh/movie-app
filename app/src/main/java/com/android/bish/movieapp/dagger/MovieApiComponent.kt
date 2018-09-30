package com.android.bish.movieapp.dagger

import com.android.bish.movieapp.api.MovieApi
import com.squareup.picasso.Picasso
import dagger.Component

@Component(modules = arrayOf(MovieApiModule::class, PicassoModule::class))
interface MovieApiComponent {

    val movieApi: MovieApi

    val picasso: Picasso
}