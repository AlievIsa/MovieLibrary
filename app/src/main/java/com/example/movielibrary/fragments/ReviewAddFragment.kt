package com.example.movielibrary.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movielibrary.R
import com.example.movielibrary.data.UserManager
import com.example.movielibrary.databinding.FragmentReviewAddBinding
import com.example.movielibrary.models.Review
import com.example.movielibrary.sqlite.SQLiteHelper
import com.google.api.LogDescriptor

class ReviewAddFragment : DialogFragment() {

    private lateinit var binding: FragmentReviewAddBinding
    private val args: ReviewAddFragmentArgs by navArgs()
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentReviewAddBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        sqLiteHelper = context?.let { SQLiteHelper(it) }!!

        binding.apply {
            closeButton.setOnClickListener {
                findNavController().popBackStack()
            }
            movieName.text = args.movieName
            addButton.isEnabled = false
            reviewText.addTextChangedListener {
                addButton.isEnabled = if (reviewText.text.isEmpty()) false else true
            }
            addButton.setOnClickListener {
                addReview()
            }
        }

        builder.setView(binding.root)
        return  builder.create()
    }

    private fun addReview() {
        val review = Review(userId = UserManager.currentUser.id,
            movieName = binding.movieName.text.toString() ,
            text = binding.reviewText.text.toString())
        val status = sqLiteHelper.insertReview(review)
        // Проверка на успешное добавление отзыва
        if (status > -1) {
            Toast.makeText(context, getString(R.string.review_added), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Review not added", Toast.LENGTH_SHORT).show()
        }
        val reviewList = sqLiteHelper.getAllReview()
        Log.d("BBB", "${reviewList.size}")
        findNavController().popBackStack()
    }

}