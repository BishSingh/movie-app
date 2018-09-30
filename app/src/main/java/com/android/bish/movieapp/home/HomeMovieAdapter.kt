package com.android.bish.movieapp.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.bish.movieapp.R
import com.android.bish.movieapp.model.MovieItemDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_row_item.view.*

class HomeMovieAdapter(private var movies: List<MovieItemDetails>, private val picasso: Picasso, private val listener : OnItemClickListener) : RecyclerView.Adapter<HomeMovieAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onMovieClicked(title: String?, backdropPath : String?, plot : String?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.movie_row_item, viewGroup, false)

        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(movies[position], picasso, listener)

    override fun getItemCount(): Int {
        return movies.size
    }

    fun refresh(movies: List<MovieItemDetails>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w780"

        fun bind(movie: MovieItemDetails, picasso: Picasso, listener: OnItemClickListener) = with(itemView) {
            picasso.load(POSTER_IMAGE_BASE_URL + movie.poster_path).into(movie_image)
            movie_image.setOnClickListener {listener.onMovieClicked(movie.title, movie.backdrop_path, movie.overview)}
        }
    }
}