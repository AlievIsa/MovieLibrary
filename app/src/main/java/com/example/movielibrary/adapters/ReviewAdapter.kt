package com.example.movielibrary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movielibrary.databinding.ReviewItemBinding
import com.example.movielibrary.models.Review

class ReviewAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    private var reviewList: ArrayList<Review> = ArrayList()

    fun addItems(items: ArrayList<Review>) {
        this.reviewList = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ReviewViewHolder(private val binding: ReviewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            fun bind(review: Review) {
                binding.apply {
                    movieName.text = review.movieName
                    reviewText.text = review.text
                }
                itemView.setOnClickListener {
                    onItemClick(review.id)
                }
            }
    }
}