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

/**
 * Home adapter
 * Этот адаптер используется для отображения списка всех фильмов.
 * @property options - опции для FirestoreRecyclerAdapter, в котором хранятся данные
 * @property onItemClick - лямбда-выражение, которое вызывается при нажатии на элемент списка
 * @constructor Создает пустой адаптер чатов
 */
class HomeAdapter(
    options: FirestoreRecyclerOptions<Movie>,
    private val onItemClick: (String) -> Unit
) : FirestoreRecyclerAdapter<Movie, HomeAdapter.MovieViewHolder>(options) {

    /**
     * On create view holder
     * Этот метод вызывается при создании ViewHolder'а, в нем создается View
     * @param parent - родительский ViewGroup
     * @param viewType - тип View
     * @return возвращает созданный ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    /**
     * On bind view holder
     * Этот метод вызывается при привязке ViewHolder'а к определенной позиции.
     * В нем происходит привязка данных к View
     * @param holder - ViewHolder, к которому привязываются данные
     * @param position - позиция, к которой привязываются данные
     * @param model - модель, из которой берутся данные
     */
    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int,
        model: Movie
    ) {
        holder.bind(model)
    }

    /**
     * Movie view holder
     * Этот класс используется для хранения View элемента списка
     * @constructor
     * @param binding - привязка к View
     */
    inner class MovieViewHolder(private val binding: HomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind
         * Этот метод используется для привязки данных к View
         * @param movie - модель, из которой берутся данные
         */
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