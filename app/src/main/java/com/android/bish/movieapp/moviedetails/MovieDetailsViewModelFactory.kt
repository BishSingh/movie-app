package com.android.bish.movieapp.moviedetails

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class MovieDetailsViewModelFactory(private val title: String?, private val backdropPath: String?, private val plot: String?) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MovieDetailsViewModel(title, backdropPath, plot) as T
    }
}
