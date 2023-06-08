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

/**
 * Search fragment
 * Этот фрагмент используется для отображения всех фильмов и осуществления поиска фильма по названию.
 * @constructor создает пустой фрагмент чатов
 */
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val db = Firebase.firestore

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
        binding = FragmentSearchBinding.inflate(layoutInflater)
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

        initAllMoviesRecyclerView()
        setUpSearch()
    }

    /**
     * Init all movies recycler view
     * Этот метод инициализирует основное представление объекта RecyclerView со всеми фильмами.
     */
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

    /**
     * Setup search
     * Этот метод настраивает представления поиска и повторного просмотра результатов поиска.
     */
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

    /**
     * Perform search with query
     * Этот метод выполняет поиск по заданному тексту поиска.
     * @param searchText - текст, по которому проводится поиск
     */
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

    /**
     * Set up movies recycler view
     * Этот метод устанавливет RecyclerView по заданному запросу
     * @param recyclerView
     * @param moviesQuery - полученный запрос
     */
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

    /**
     * Handle movies ui
     * Этот метод обрабатывет видимость чата
     * @param isMovieFound - переменная, отвечающая за нахождения фильмов.
     */
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

    /**
     * Handle search results ui
     * Этот метод обрабатывет видимоcть результатов поиска
     * @param isMovieFound - переменная, отвечающая за нахождения фильмов.
     */
    private fun handleSearchResultsUi(isMovieFound: Boolean) {
        // Обновление пользовательского интерфейса, чтобы показать, что результаты поиска пусты
        // Например, отображение сообщения или скрытие результатов поиска в RecyclerView
        binding.apply {
            searchResultsRecyclerView.visibility = if (isMovieFound) View.VISIBLE else View.GONE
            emptySearchResultsTextView.visibility = if (isMovieFound) View.GONE else View.VISIBLE
        }
    }
}
