package com.example.movielibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.movielibrary.R
import com.example.movielibrary.data.KEY_COLLECTION_USERS
import com.example.movielibrary.data.KEY_IS_ADMIN
import com.example.movielibrary.data.UserManager
import com.example.movielibrary.databinding.FragmentReviewsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviewsFragment : Fragment() {

    private lateinit var binding: FragmentReviewsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewsBinding.inflate(layoutInflater)
        return binding.root
    }

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
        }
    }
}