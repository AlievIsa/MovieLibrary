package com.example.movielibrary.fragments

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.movielibrary.R
import com.example.movielibrary.data.KEY_COLLECTION_MOVIES
import com.example.movielibrary.data.KEY_MOVIE_DESCRIPTION
import com.example.movielibrary.data.KEY_MOVIE_GENRE
import com.example.movielibrary.data.KEY_MOVIE_NAME
import com.example.movielibrary.data.KEY_MOVIE_POSTER
import com.example.movielibrary.data.KEY_MOVIE_RATE
import com.example.movielibrary.data.KEY_MOVIE_RELEASE_YEAR
import com.example.movielibrary.databinding.FragmentMovieBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MovieFragment : Fragment() {

    private lateinit var binding: FragmentMovieBinding
    private val db = Firebase.firestore
    private val args: MovieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.movieToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        db.collection(KEY_COLLECTION_MOVIES).document(args.movieName)
            .get().addOnSuccessListener { movieSnapShot ->
                binding.apply {
                    movieName.text = movieSnapShot.getString(KEY_MOVIE_NAME)
                    movieReleaseYear.text = getString(R.string.release_year) + " " +
                            movieSnapShot.getString(KEY_MOVIE_RELEASE_YEAR)
                    movieGenre.text = getString(R.string.genre) + " " +
                            movieSnapShot.getString(KEY_MOVIE_GENRE)
                    movieRate.text = getString(R.string.movie_rate) + " " +
                            movieSnapShot.getString(KEY_MOVIE_RATE)
                    description.text = movieSnapShot.getString(KEY_MOVIE_DESCRIPTION)
                }
                Glide.with(this)
                    .load(movieSnapShot.getString(KEY_MOVIE_POSTER))
                    .placeholder(R.drawable.round_error_outline_24)
                    .into(binding.moviePoster)
            }
    }
}