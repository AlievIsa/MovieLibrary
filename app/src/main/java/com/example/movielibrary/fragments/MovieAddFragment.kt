package com.example.movielibrary.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.movielibrary.R
import com.example.movielibrary.data.KEY_COLLECTION_MOVIES
import com.example.movielibrary.data.KEY_MOVIE_DESCRIPTION
import com.example.movielibrary.data.KEY_MOVIE_GENRE
import com.example.movielibrary.data.KEY_MOVIE_NAME
import com.example.movielibrary.data.KEY_MOVIE_POSTER
import com.example.movielibrary.data.KEY_MOVIE_RATE
import com.example.movielibrary.data.KEY_MOVIE_RELEASE_YEAR
import com.example.movielibrary.databinding.FragmentMovieAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

/**
 * Movie add fragment
 * Этот фрагмент используется для добавления фильма в базу данных администратором.
 * @constructor создает пустой фрагмент пользователя
 */
class MovieAddFragment : DialogFragment() {

    private lateinit var binding: FragmentMovieAddBinding
    private lateinit var moviePosterUri: Uri
    private var db = Firebase.firestore

    /**
     * On create dialog
     * Этот метод создает собственный пользовательский диалоговый контейнер
     * @param savedInstanceState - объект, необходимый для сохранения состояний
     * @return новый экземпляр диалогового окна, который будет отображаться фрагментом.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        binding = FragmentMovieAddBinding.inflate(layoutInflater)

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val genres = arrayOf("Фантастика", "Боевики", "Приключения", "Триллеры", "Драма", "Ужасы")
        val adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, genres) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genreSpinner.adapter = adapter

        val pickImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val uri = data?.data
                    moviePosterUri = uri!!
                    binding.moviePoster.setImageURI(uri)
                    binding.chooseMoviePoster.visibility = View.GONE
                }
            }

        binding.moviePoster.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImage.launch(intent)
        }

        binding.addMovieButton.isEnabled = false

        val textWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.apply {
                    val edit1 = movieNameEditText.text.toString().isEmpty()
                    val edit2 = movieRateEditText.text.toString().isEmpty()
                    val edit3 = movieDescriptionEditText.text.toString().isEmpty()
                    val edit4 = movieReleaseYearEditText.text.toString().isEmpty()
                    addMovieButton.isEnabled = !edit1 && !edit2 &&
                            !edit3 && !edit4 && ::moviePosterUri.isInitialized
                }
            }
        }

        binding.apply {
            movieNameEditText.addTextChangedListener(textWatcher)
            movieRateEditText.addTextChangedListener(textWatcher)
            movieDescriptionEditText.addTextChangedListener(textWatcher)
            movieReleaseYearEditText.addTextChangedListener(textWatcher)
        }

        binding.addMovieButton.setOnClickListener{
            addMovie(
                name = binding.movieNameEditText.text.toString().trim(),
                rate = binding.movieRateEditText.text.toString().trim(),
                poster = moviePosterUri,
                description = binding.movieDescriptionEditText.text.toString(),
                genre = binding.genreSpinner.selectedItem.toString(),
                releaseYear = binding.movieReleaseYearEditText.text.toString()
            )
            findNavController().popBackStack()
        }

        builder.setView(binding.root)
        return builder.create()
    }

    /**
     * Add movie
     * Данный метод создает в базе данных фильм по соответствующим аргументам
     * @param name - название фильма
     * @param rate - оценка фильма
     * @param poster - постер фильма
     * @param description - описание фильма
     * @param genre - жанр фильма
     * @param releaseYear - год выхода фильма
     */
    private fun addMovie(
        name: String,
        rate: String,
        poster: Uri,
        description: String,
        genre: String,
        releaseYear: String
    ) {
        val movieMap = hashMapOf(
            KEY_MOVIE_NAME to name,
            KEY_MOVIE_GENRE to genre,
            KEY_MOVIE_RATE to rate,
            KEY_MOVIE_DESCRIPTION to description,
            KEY_MOVIE_RELEASE_YEAR to releaseYear
        )
        db.collection(KEY_COLLECTION_MOVIES).document(name).set(movieMap)

        // Добавление URL-адреса аватара в хранилище аватаров
        // Получение ссылки на файл хранилища в avatars/<FILENAME>
        val storageRef =
            FirebaseStorage.getInstance().reference.child("posters/${name}.jpg")
        // Загрузка файла в хранилище Firebase
        storageRef.putFile(poster)
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        db.collection(KEY_COLLECTION_MOVIES).document(name)
                            .update(KEY_MOVIE_POSTER, it.toString())
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}
