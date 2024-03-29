package com.example.movielibrary.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.movielibrary.R
import com.example.movielibrary.activities.MainActivity
import com.example.movielibrary.data.KEY_COLLECTION_USERS
import com.example.movielibrary.data.KEY_EMAIL
import com.example.movielibrary.data.KEY_ID
import com.example.movielibrary.data.KEY_NAME
import com.example.movielibrary.data.KEY_PROFILE_PICTURE
import com.example.movielibrary.data.UserManager
import com.example.movielibrary.databinding.FragmentLogInBinding
import com.example.movielibrary.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(layoutInflater)
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

        // Проверяем, авторизован ли пользователь
        // Для этого проверяем значение isLoggedIn в SharedPreferences
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // Скрыть фрагмент logIn
            parentFragmentManager.beginTransaction().hide(this).commit()
            val usersRef = db.collection(KEY_COLLECTION_USERS)
            usersRef.whereEqualTo(KEY_ID, auth.currentUser?.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        for (doc in document) {
                            UserManager.currentUser = User(
                                id = doc.data[KEY_ID] as String,
                                email = doc.data[KEY_EMAIL] as String,
                                name = doc.data[KEY_NAME] as String,
                                profile_picture = doc.data[KEY_PROFILE_PICTURE] as String
                            )
                        }
                        val intent = Intent(activity, MainActivity::class.java)
                        activity?.startActivity(intent)
                    }
                }
        }
        setEditTextListeners()
        // Обработка нажатия на кнопку входа в систему
        binding.logInButton.setOnClickListener {
            if (isValidDetails()) { // Если данные корректны
                logIn(
                    binding.loginInputEmail.editText?.text.toString(),
                    binding.loginInputPassword.editText?.text.toString()
                )
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }
    }

    /**
     * Set edit text listeners
     * Установка слушателей для обработки ввода почты и пароля
     */
    private fun setEditTextListeners() {
        binding.apply {
            loginInputEmail.editText?.addTextChangedListener { name ->
                checkEmail(name.toString())
            }
            loginInputPassword.editText?.addTextChangedListener { password ->
                checkPassword(password.toString())
            }
        }
    }

    /**
     * Check email
     * Этот метод используется для проверки корректности введенной почты.
     * @param email
     */
    private fun checkEmail(email: String): Boolean {
        return if (email.trim().isEmpty()) {
            binding.loginInputEmail.error = getString(R.string.email_error)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginInputEmail.error = getString(R.string.invalid_email)
            false
        } else {
            binding.loginInputEmail.isErrorEnabled = false
            true
        }
    }

    /**
     * Check password
     * Этот метод используется для проверки корректности введенного пароля.
     * @param password
     */
    private fun checkPassword(password: String): Boolean {
        // Шаблон регулярного выражения для соответствия буквам, цифрам и специальным символам
        val passwordRegex = "^[a-zA-Z0-9@#\$%^&+=]+$"

        return when {
            password.trim().isEmpty() -> {
                binding.loginInputPassword.error = getString(R.string.password_error)
                false
            }

            password.length < 6 || password.length > 24 -> {
                binding.loginInputPassword.error = getString(R.string.password_length_error)
                false
            }

            !password.matches(passwordRegex.toRegex()) -> {
                binding.loginInputPassword.error = getString(R.string.password_invalid_characters_error)
                false
            }

            else -> {
                binding.loginInputPassword.isErrorEnabled = false
                true
            }
        }
    }

    /**
     * Is valid details
     * Этот метод проверяет достоверность введенных данных.
     * @return Boolean
     */
    private fun isValidDetails(): Boolean {
        binding.apply {
            return checkEmail(loginInputEmail.editText?.text.toString()) &&
                    checkPassword(loginInputPassword.editText?.text.toString())
        }
    }

    /**
     * Log in
     * Этот метод используется для входа в приложение.
     * @param email
     * @param password
     */
    private fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val usersRef = db.collection(KEY_COLLECTION_USERS)
                    usersRef.whereEqualTo(KEY_ID, auth.currentUser?.uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                for (doc in document) {
                                    UserManager.currentUser = User(
                                            id = doc.data[KEY_ID] as String,
                                            email = doc.data[KEY_EMAIL] as String,
                                            name = doc.data[KEY_NAME] as String,
                                            profile_picture = doc.data[KEY_PROFILE_PICTURE] as String
                                        )
                                }
                                // Сохранение статуса входа в систему в SharedPreferences
                                val sharedPreferences = requireActivity().getSharedPreferences(
                                    "MyPrefs", Context.MODE_PRIVATE
                                )
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                // Успешный вход в систему,
                                // обновление пользовательского интерфейса информацией о вошедшем пользователе
                                val intent = Intent(activity, MainActivity::class.java)
                                // Запрет пользователю возвращаться к экрану входа в систему
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                activity?.startActivity(intent)
                            } else {
                                // Создадим тост
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.user_not_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            // Создадим тост
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.answer_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                } else {
                    Toast.makeText(context, getString(R.string.log_in_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}