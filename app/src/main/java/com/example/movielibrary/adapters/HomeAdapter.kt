package com.example.movielibrary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movielibrary.R
import com.example.movielibrary.databinding.HomeItemBinding
import com.example.movielibrary.models.Movie
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HomeAdapter(
    options: FirestoreRecyclerOptions<Movie>,
    private val onItemClick: (String) -> Unit
) : FirestoreRecyclerAdapter<Movie, HomeAdapter.MovieViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int,
        model: Movie
    ) {
        holder.bind(model)
    }

    inner class MovieViewHolder(private val binding: HomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(movie: Movie) {
            binding.movieName.text = movie.name
            Glide.with(itemView.context)
                .load(movie.poster)
                .placeholder(R.drawable.round_error_outline_24)
                .into(binding.moviePoster)

            itemView.setOnClickListener {
                onItemClick(movie.name)
            }
        }
    }
}