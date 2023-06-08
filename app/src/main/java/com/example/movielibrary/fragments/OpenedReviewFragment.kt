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
import com.example.movielibrary.databinding.FragmentOpenedReviewBinding
import com.example.movielibrary.models.Review
import com.example.movielibrary.sqlite.SQLiteHelper


class OpenedReviewFragment : DialogFragment() {

    private lateinit var binding: FragmentOpenedReviewBinding
    private val args: OpenedReviewFragmentArgs by navArgs()
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentOpenedReviewBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        sqLiteHelper = context?.let { SQLiteHelper(it) }!!

        // Получение переданных аргументов
        val reviewId = arguments?.getInt("reviewId", -1) ?: -1
        val reviewsFragment = targetFragment as? ReviewsFragment

        binding.apply {
            closeButton.setOnClickListener {
                reviewsFragment?.updateReviewsList() // Обновление списка отзывов
                dismiss() // Закрытие диалогового окна
            }
            val review = sqLiteHelper.getReviewById(reviewId)
            movieName.text = review?.movieName
            reviewText.setText(review?.text)
            saveButton.isEnabled = false
            reviewText.addTextChangedListener {
                saveButton.isEnabled = !reviewText.text.isEmpty()
            }
            saveButton.setOnClickListener {
                review?.text = reviewText.text.toString()
                sqLiteHelper.updateReview(review!!)
                Toast.makeText(context, getString(R.string.review_changed), Toast.LENGTH_SHORT).show()
                reviewsFragment?.updateReviewsList() // Обновление списка отзывов
                dismiss() // Закрытие диалогового окна
            }
            deleteButton.setOnClickListener {
                sqLiteHelper.deleteReviewById(reviewId)
                Toast.makeText(context, getString(R.string.review_deleted), Toast.LENGTH_SHORT).show()
                reviewsFragment?.updateReviewsList() // Обновление списка отзывов
                dismiss() // Закрытие диалогового окна
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

}