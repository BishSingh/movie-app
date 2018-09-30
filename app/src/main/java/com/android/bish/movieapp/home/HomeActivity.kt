package com.android.bish.movieapp

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.android.bish.movieapp.home.HomeMovieAdapter
import com.android.bish.movieapp.model.MovieItemDetails
import com.android.bish.movieapp.moviedetails.MovieDetails
import com.android.bish.movieapp.viewmodel.HomeViewModel
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Class is responsible for Displaying now playing movies and searching movies.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var homeMovieAdapter: HomeMovieAdapter

    private lateinit var homeViewModel: HomeViewModel

    private val searchTerm = PublishSubject.create<String>()

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val picasso = MovieApplication.get(this).movieApplicationComponent.picasso

        supportActionBar?.title = "Now Playing"

        configureViewModel()
        configureAdapter(picasso)

        /**
         * Using Debounce operator here so that we don't make service calls multiple times and input is
         * read only after a delay of 250ms
         */
        searchTerm.debounce(250, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { test -> !test.isEmpty() }
                .distinctUntilChanged()
                .subscribe { result -> handleSearch(result) }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchTerm.onNext(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchTerm.onNext(newText)
                return false
            }
        })
        return true
    }

    private fun configureViewModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.init()
        compositeDisposable.add(homeViewModel.getMovieList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> updateSearchResults(result) })

        compositeDisposable.add(homeViewModel.isLoading()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> handleLoadingState(result) })

        compositeDisposable.add(homeViewModel.hasLoadingFailed()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ -> handleErrorState(true) })
    }

    private fun configureAdapter(picasso: Picasso) {
        val movieList = ArrayList<MovieItemDetails>()

        val obj = object : HomeMovieAdapter.OnItemClickListener {
            override fun onMovieClicked(title: String?, backdropPath: String?, plot: String?) {
                startMovieDetailsActivity(title, backdropPath, plot)
            }
        }

        homeMovieAdapter = HomeMovieAdapter(movieList, picasso, obj)

        val layoutManager = GridLayoutManager(this, 2)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = homeMovieAdapter
    }

    private fun handleSearch(searchTerm: String) {
        supportActionBar?.title = "Search Results"
        homeViewModel.fetchSearchMovies(searchTerm)
    }

    private fun updateSearchResults(productItemDetails: List<MovieItemDetails>) {
        this.homeMovieAdapter.refresh(productItemDetails)
    }

    private fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            recycler_view.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
            error_layout.visibility = View.GONE
        } else {
            recycler_view.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
            error_layout.visibility = View.GONE
        }
    }

    private fun handleErrorState(hasLoadingFailed: Boolean) {
        if (hasLoadingFailed) {
            error_layout.visibility = View.VISIBLE
            recycler_view.visibility = View.GONE
        } else {
            error_layout.visibility = View.GONE
        }
    }

    private fun startMovieDetailsActivity(title: String?, backdropPath: String?, plot: String?) {
        val intent = Intent(this, MovieDetails::class.java)
        intent.putExtra(TITLE_EXTRA, title)
        intent.putExtra(BACKDROP_EXTRA, backdropPath)
        intent.putExtra(PLOT_EXTRA, plot)
        startActivity(intent)
    }

    companion object {

        val TITLE_EXTRA = "title.extra"

        val BACKDROP_EXTRA = "backdrop.extra"

        val PLOT_EXTRA = "plot.extra"
    }
}
