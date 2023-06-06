package com.example.movielibrary.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.movielibrary.R
import com.example.movielibrary.activities.EntranceActivity
import com.example.movielibrary.data.KEY_COLLECTION_USERS
import com.example.movielibrary.data.KEY_IS_ADMIN
import com.example.movielibrary.data.UserManager
import com.example.movielibrary.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = UserManager.currentUser
        val userRef = Firebase.firestore.collection(KEY_COLLECTION_USERS).document(user.id)
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Обработка ошибки
                return@addSnapshotListener
            }
            if (snapshot?.getBoolean(KEY_IS_ADMIN) == true)
                binding.addMovie.visibility = View.VISIBLE
            else
                binding.addMovie.visibility = View.GONE
        }
        setListeners()
    }

    private fun setListeners() {
        binding.chatToolbar.setNavigationOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            val intent = Intent(activity, EntranceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity?.startActivity(intent)
        }
    }
}