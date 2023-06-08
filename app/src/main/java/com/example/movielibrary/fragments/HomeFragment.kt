package com.example.movielibrary.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielibrary.R
import com.example.movielibrary.activities.EntranceActivity
import com.example.movielibrary.adapters.HomeAdapter
import com.example.movielibrary.data.KEY_ACTION
import com.example.movielibrary.data.KEY_ADVENTURE
import com.example.movielibrary.data.KEY_COLLECTION_MOVIES
import com.example.movielibrary.data.KEY_COLLECTION_USERS
import com.example.movielibrary.data.KEY_DRAMA
import com.example.movielibrary.data.KEY_FANTASY
import com.example.movielibrary.data.KEY_HORRORS
import com.example.movielibrary.data.KEY_IS_ADMIN
import com.example.movielibrary.data.KEY_MOVIE_GENRE
import com.example.movielibrary.data.KEY_THRILLERS
import com.example.movielibrary.data.UserManager
import com.example.movielibrary.databinding.FragmentHomeBinding
import com.example.movielibrary.models.Movie
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val db = Firebase.firestore
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
        initRecyclerViews()
    }

    private fun setListeners() {
        binding.homeToolbar.setNavigationOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            val intent = Intent(activity, EntranceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity?.startActivity(intent)
        }
        binding.addMovie.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_movieAddFragment)
        }
    }

    private fun initRecyclerViews() {
        val fantasyQuery = db.collection(KEY_COLLECTION_MOVIES)
            .whereEqualTo(KEY_MOVIE_GENRE, KEY_FANTASY)

        val fantasyOptions = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(fantasyQuery, Movie::class.java).build()

        val fantasyAdapter = HomeAdapter(fantasyOptions) { movieName ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        fantasyAdapter.startListening()
        binding.fantasyRecyclerView.adapter = fantasyAdapter
        val layoutManager1 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.fantasyRecyclerView.layoutManager = layoutManager1


        val actionQuery = db.collection(KEY_COLLECTION_MOVIES)
            .whereEqualTo(KEY_MOVIE_GENRE, KEY_ACTION)

        val actionOptions = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(actionQuery, Movie::class.java).build()

        val actionAdapter = HomeAdapter(actionOptions) { movieName ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        actionAdapter.startListening()
        binding.actionRecyclerView.adapter = actionAdapter
        val layoutManager2 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.actionRecyclerView.layoutManager = layoutManager2


        val adventureQuery = db.collection(KEY_COLLECTION_MOVIES)
            .whereEqualTo(KEY_MOVIE_GENRE, KEY_ADVENTURE)

        val adventureOptions = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(adventureQuery, Movie::class.java).build()

        val adventureAdapter = HomeAdapter(adventureOptions) { movieName ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        adventureAdapter.startListening()
        binding.adventureRecyclerView.adapter = adventureAdapter
        val layoutManager3 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.adventureRecyclerView.layoutManager = layoutManager3


        val thrillerQuery = db.collection(KEY_COLLECTION_MOVIES)
            .whereEqualTo(KEY_MOVIE_GENRE, KEY_THRILLERS)

        val thrillerOptions = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(thrillerQuery, Movie::class.java).build()

        val thrillerAdapter = HomeAdapter(thrillerOptions) { movieName ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        thrillerAdapter.startListening()
        binding.thrillerRecyclerView.adapter = thrillerAdapter
        val layoutManager4 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.thrillerRecyclerView.layoutManager = layoutManager4


        val dramaQuery = db.collection(KEY_COLLECTION_MOVIES)
            .whereEqualTo(KEY_MOVIE_GENRE, KEY_DRAMA)

        val dramaOptions = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(dramaQuery, Movie::class.java).build()

        val dramaAdapter = HomeAdapter(dramaOptions) { movieName ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        dramaAdapter.startListening()
        binding.dramaRecyclerView.adapter = dramaAdapter
        val layoutManager5 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.dramaRecyclerView.layoutManager = layoutManager5


        val horrorQuery = db.collection(KEY_COLLECTION_MOVIES)
            .whereEqualTo(KEY_MOVIE_GENRE, KEY_HORRORS)

        val horrorOptions = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(horrorQuery, Movie::class.java).build()

        val horrorAdapter = HomeAdapter(horrorOptions) { movieName ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        horrorAdapter.startListening()
        binding.horrorRecyclerView.adapter = horrorAdapter
        val layoutManager6 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.horrorRecyclerView.layoutManager = layoutManager6
    }
}