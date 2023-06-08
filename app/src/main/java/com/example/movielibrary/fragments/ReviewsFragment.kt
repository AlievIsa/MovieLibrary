package com.example.movielibrary.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movielibrary.R
import com.example.movielibrary.adapters.ReviewAdapter
import com.example.movielibrary.data.KEY_COLLECTION_USERS
import com.example.movielibrary.data.KEY_IS_ADMIN
import com.example.movielibrary.data.UserManager
import com.example.movielibrary.databinding.FragmentReviewsBinding
import com.example.movielibrary.sqlite.SQLiteHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Reviews fragment
 * Этот фрагмент используется для отображения созданных отзывов.
 * @constructor создает пустой фрагмент чатов
 */
class ReviewsFragment : Fragment() {

    private lateinit var binding: FragmentReviewsBinding
    private lateinit var sqLiteHelper: SQLiteHelper

    /**
     * On create view
     * Этот метод устанавливает представление фрагмента
     * @param inflater - объект, который раздувает все элементы view на фрагменте
     * @param savedInstanceState - объект, необходимый для сохранения состояний
     * @return возвращает созданное представление
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewsBinding.inflate(layoutInflater)
        sqLiteHelper = context?.let { SQLiteHelper(it) }!!
        return binding.root
    }

    /**
     * On view created
     * Этот метод вызывается сразу после установки представления
     * @param view - представление полученное из метода onCreateView
     * @param savedInstanceState - объект, необходимый для сохранения состояний
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = UserManager.currentUser
        Glide.with(this)
            .load(user.profile_picture)
            .placeholder(R.drawable.baseline_account_circle_24)
            .into(binding.avatarImageView)
        binding.nameTextView.text = user.name
        val userRef = Firebase.firestore.collection(KEY_COLLECTION_USERS).document(user.id)
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Обработка ошибки
                return@addSnapshotListener
            }
            binding.roleTextView.text = if (snapshot?.getBoolean(KEY_IS_ADMIN) == true)
                getString(R.string.admin)
            else
                getString(R.string.user)
            initReviewsRecyclerView()
        }
    }

    private fun initReviewsRecyclerView() {
        val reviewList = sqLiteHelper.getUserReviews(UserManager.currentUser.id)
        val adapter = ReviewAdapter() { reviewId ->
            val dialogFragment = OpenedReviewFragment()
            dialogFragment.setTargetFragment(this, 0) // Установка текущего фрагмента в качестве целевого фрагмента
            val bundle = Bundle()
            bundle.putInt("reviewId", reviewId)
            dialogFragment.arguments = bundle
            dialogFragment.show(parentFragmentManager, "OpenedReviewFragment")
        }
        adapter.addItems(reviewList)
        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.reviewsRecyclerView.adapter = adapter
    }

    fun updateReviewsList() {
        val reviewList = sqLiteHelper.getUserReviews(UserManager.currentUser.id)
        (binding.reviewsRecyclerView.adapter as? ReviewAdapter)?.apply {
            addItems(reviewList)
            notifyDataSetChanged()
        }
    }
}