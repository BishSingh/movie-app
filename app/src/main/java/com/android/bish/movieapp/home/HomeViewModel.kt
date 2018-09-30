package com.android.bish.movieapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.android.bish.movieapp.MovieApplication
import com.android.bish.movieapp.api.MovieApi
import com.android.bish.movieapp.model.MovieItemDetails
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Uses AndroidViewModel from Architecture Components so that data state is maintained even when
 * activity is restarted for scenarios like phone orientation change
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val movieList = PublishSubject.create<List<MovieItemDetails>>()

    private val isLoading = PublishSubject.create<Boolean>()

    private val error = PublishSubject.create<String>()

    private val compositeDisposable = CompositeDisposable()

    private val movieApi: MovieApi = (application as MovieApplication).movieApplicationComponent.movieApi

    fun init() {
        isLoading.onNext(true)
        fetchNowPlaying()
    }

    fun isLoading(): Observable<Boolean> {
        return isLoading
    }

    fun hasLoadingFailed(): Observable<String> {
        return error
    }

    fun getMovieList(): Observable<List<MovieItemDetails>> {
        return movieList
    }

    private fun fetchNowPlaying() {
        compositeDisposable.add(movieApi.nowPlaying()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    isLoading.onNext(false)
                    movieList.onNext(result.results)
                },
                        { _ ->
                            isLoading.onNext(false)
                            error.onNext("No Movies Found")
                        }))
    }

    fun fetchSearchMovies(searchTerm : String) {
        compositeDisposable.add(movieApi.searchMovies(searchTerm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    isLoading.onNext(false)
                    if(!result.results.isEmpty()) {
                        movieList.onNext(result.results)
                    } else {
                        error.onNext("No Movies Found")
                    }
                },
                        { _ ->
                            isLoading.onNext(false)
                            error.onNext("No Movies Found")
                        }))
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
