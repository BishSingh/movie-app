package com.android.bish.movieapp.api

import com.android.bish.movieapp.model.MovieNowPlaying
import com.android.bish.movieapp.model.SearchResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/now_playing")
    fun nowPlaying(): Single<MovieNowPlaying>

    @GET("search/movie")
    fun searchMovies(@Query("query") query: String) : Single<SearchResult>

}