package com.android.bish.movieapp.moviedetails

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable

class MovieDetailsViewModel(private var title : String?, private var backdropPath : String?, private var plot : String?) : ViewModel() {

    fun title(): Observable<String> {
        if(title != null) {
            return Observable.just(title)
        }
        return Observable.just("")
    }

    fun backDropPath(): Observable<String> {
        if(backdropPath != null) {
            return Observable.just(backdropPath)
        }
        return Observable.just("")
    }

    fun plot(): Observable<String> {
        if(plot != null) {
            return Observable.just(plot)
        }
        return Observable.just("")
    }
}