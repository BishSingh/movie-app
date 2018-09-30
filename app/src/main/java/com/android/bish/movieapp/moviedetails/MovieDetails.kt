package com.android.bish.movieapp.moviedetails

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.bish.movieapp.HomeActivity
import com.android.bish.movieapp.MovieApplication
import com.android.bish.movieapp.R
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_movie_details.*

/**
 * Class is responsible for Displaying movie details.
 */
class MovieDetails : AppCompatActivity() {

    private val BACKDROP_IMAGE_BASE_URL  = "https://image.tmdb.org/t/p/w1280"

    private lateinit var movieDetailsViewModel : MovieDetailsViewModel

    private var title : String? = null

    private var backdropPath : String? = null

    private var plot : String? = null

    private var compositeDisposable = CompositeDisposable()

    private lateinit var picasso : Picasso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        supportActionBar?.title = "Movie Details"

        picasso = MovieApplication.get(this).movieApplicationComponent.picasso

        val intent = intent
        title = intent.getStringExtra(HomeActivity.TITLE_EXTRA)
        backdropPath = intent.getStringExtra(HomeActivity.BACKDROP_EXTRA)
        plot = intent.getStringExtra(HomeActivity.PLOT_EXTRA)

        configureViewModel()
    }

    private fun configureViewModel() {
        movieDetailsViewModel = ViewModelProviders.of(this, MovieDetailsViewModelFactory(title, backdropPath, plot)).get(MovieDetailsViewModel::class.java)
        compositeDisposable.add(movieDetailsViewModel.title()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { title -> title_view. setText(title)})

        compositeDisposable.add(movieDetailsViewModel.backDropPath()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { path -> loadBackdrop(path) })

        compositeDisposable.add(movieDetailsViewModel.plot()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result-> plot_view.setText(result) })
    }

    private fun loadBackdrop(path : String) {
        picasso.load(BACKDROP_IMAGE_BASE_URL + path)
                .error(R.drawable.ic_broken_image)
                .into(backdrop_image)

    }
}