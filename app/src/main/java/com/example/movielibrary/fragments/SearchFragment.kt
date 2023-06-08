package com.example.movielibrary.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movielibrary.adapters.AllMoviesAdapter
import com.example.movielibrary.data.KEY_COLLECTION_MOVIES
import com.example.movielibrary.data.KEY_MOVIE_NAME
import com.example.movielibrary.databinding.FragmentSearchBinding
import com.example.movielibrary.models.Movie
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAllMoviesRecyclerView()
        setUpSearch()
    }


    private fun initAllMoviesRecyclerView() {
        val query = db.collection(KEY_COLLECTION_MOVIES)
        val options = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(query, Movie::class.java).build()
        val adapter = AllMoviesAdapter(options) { movieName ->
            val action = SearchFragmentDirections.actionSearchFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        adapter.startListening()
        binding.allMoviesRecyclerView.adapter = adapter
        binding.allMoviesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setUpSearch() {
        binding.apply {
            searchView.editText.addTextChangedListener {
                if (it.toString().isEmpty()) {
                    handleSearchResultsUi(false)
                } else {
                    performSearchWithQuery(searchText = it.toString())
                }
            }
        }
    }

    private fun performSearchWithQuery(searchText: String) {
        db.collection(KEY_COLLECTION_MOVIES).whereGreaterThanOrEqualTo(KEY_MOVIE_NAME, searchText)
            .whereLessThanOrEqualTo(KEY_MOVIE_NAME, searchText + '\uf8ff').get()
            .addOnSuccessListener { querySnapshot ->
                val movies = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.getString(KEY_MOVIE_NAME)
                }
                Log.d("AAA", "Check: ${movies}")
                if (movies.isNotEmpty()) {
                    val query = db.collection(KEY_COLLECTION_MOVIES).whereIn(KEY_MOVIE_NAME, movies)
                    setUpMoviesRecyclerView(binding.searchResultsRecyclerView, query)
                } else {
                    handleSearchResultsUi(false)
                }
            }
    }

    private fun setUpMoviesRecyclerView(recyclerView: RecyclerView, moviesQuery: Query) {
        val options = FirestoreRecyclerOptions.Builder<Movie>()
            .setQuery(moviesQuery, Movie::class.java).build()
        val adapter = AllMoviesAdapter(options){ movieName ->
            val action = SearchFragmentDirections.actionSearchFragmentToMovieFragment(movieName)
            findNavController().navigate(action)
        }
        adapter.setOnDataChangedListener {
            when(recyclerView) {
                binding.allMoviesRecyclerView -> {
                    handleMoviesUi(adapter.itemCount > 0)
                }

                binding.searchResultsRecyclerView -> {
                    handleSearchResultsUi(adapter.itemCount > 0)
                }
            }
        }
        adapter.startListening()
        // Удаление старого адаптера из памяти
        (recyclerView.adapter as? AllMoviesAdapter)?.stopListening()
        recyclerView.adapter = null
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun handleMoviesUi(isMovieFound: Boolean) {
        binding.apply {
            if (isMovieFound) {
                allMoviesRecyclerView.visibility = View.VISIBLE
                emptySearchResultsTextView.visibility = View.GONE
            } else {
                allMoviesRecyclerView.visibility = View.GONE
                searchResultsRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun handleSearchResultsUi(isMovieFound: Boolean) {
        // Обновление пользовательского интерфейса, чтобы показать, что результаты поиска пусты
        // Например, отображение сообщения или скрытие результатов поиска в RecyclerView
        binding.apply {
            searchResultsRecyclerView.visibility = if (isMovieFound) View.VISIBLE else View.GONE
            emptySearchResultsTextView.visibility = if (isMovieFound) View.GONE else View.VISIBLE
        }
    }
}
