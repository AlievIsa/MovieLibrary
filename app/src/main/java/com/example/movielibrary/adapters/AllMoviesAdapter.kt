package com.example.movielibrary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movielibrary.R
import com.example.movielibrary.databinding.SearchItemBinding
import com.example.movielibrary.models.Movie
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AllMoviesAdapter(
    options: FirestoreRecyclerOptions<Movie>,
    private val onItemClick: (String) -> Unit
) : FirestoreRecyclerAdapter<Movie, AllMoviesAdapter.MovieViewHolder>(options) {

    // Лямбда-выражение, которое вызывается при изменении данных
    private var onDataChangedListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int,
        model: Movie
    ) {
        holder.bind(model)
    }

    inner class MovieViewHolder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(movie: Movie) {
            binding.movieName.text = movie.name
            binding.movieGenre.text = "Жанр: " + movie.genre
            Glide.with(itemView.context)
                .load(movie.poster)
                .placeholder(R.drawable.round_error_outline_24)
                .into(binding.moviePoster)

            itemView.setOnClickListener {
                onItemClick(movie.name)
            }
        }
    }

    fun setOnDataChangedListener(listener: () -> Unit) {
        onDataChangedListener = listener
    }

    override fun onDataChanged() {
        super.onDataChanged()
        onDataChangedListener?.let { it() }
    }
}