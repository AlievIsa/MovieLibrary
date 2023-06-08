package com.example.movielibrary.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.example.movielibrary.models.Review

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "reviews.db"
        private const val DATABASE_VERSION = 1
        private const val TBL_REVIEW = "tbl_review"
        private const val ID = "id"
        private const val USER_ID = "user_id"
        private const val MOVIE_NAME = "movie_name"
        private const val REVIEW_TEXT = "review_text"
     }

    override fun onCreate(db: SQLiteDatabase?) {
        val createRblReview = ("CREATE TABLE " + TBL_REVIEW + "("
                + ID + " INTEGER PRIMARY KEY," + USER_ID + " TEXT,"
                + MOVIE_NAME + " TEXT," + REVIEW_TEXT + " TEXT)")
        db?.execSQL(createRblReview)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_REVIEW")
        onCreate(db)
    }

    fun insertReview(review: Review): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, review.id)
        contentValues.put(USER_ID, review.userId)
        contentValues.put(MOVIE_NAME, review.movieName)
        contentValues.put(REVIEW_TEXT, review.text)

        val success = db.insert(TBL_REVIEW, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllReview(): ArrayList<Review> {
        val reviewList: ArrayList<Review> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_REVIEW"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var userId: String
        var movieName: String
        var reviewText: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID))
                userId = cursor.getString(cursor.getColumnIndex(USER_ID))
                movieName = cursor.getString(cursor.getColumnIndex(MOVIE_NAME))
                reviewText = cursor.getString(cursor.getColumnIndex(REVIEW_TEXT))
                val review = Review(id, userId, movieName, reviewText)
                reviewList.add(review)
            } while (cursor.moveToNext())
        }
        return reviewList
    }

    @SuppressLint("Range")
    fun getUserReviews(currentUserId: String): ArrayList<Review> {
        val reviewList: ArrayList<Review> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_REVIEW"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var userId: String
        var movieName: String
        var reviewText: String

        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getString(cursor.getColumnIndex(USER_ID))
                if (userId == currentUserId) {
                    id = cursor.getInt(cursor.getColumnIndex(ID))
                    movieName = cursor.getString(cursor.getColumnIndex(MOVIE_NAME))
                    reviewText = cursor.getString(cursor.getColumnIndex(REVIEW_TEXT))
                    val review = Review(id, userId, movieName, reviewText)
                    reviewList.add(review)
                }
            } while (cursor.moveToNext())
        }
        return reviewList
    }

    @SuppressLint("Range")
    fun getReviewById(requiredId: Int): Review? {
        val selectQuery = "SELECT * FROM $TBL_REVIEW"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var id: Int
        var userId: String
        var movieName: String
        var reviewText: String
        var review: Review
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID))
                if (requiredId == id) {
                    userId = cursor.getString(cursor.getColumnIndex(USER_ID))
                    movieName = cursor.getString(cursor.getColumnIndex(MOVIE_NAME))
                    reviewText = cursor.getString(cursor.getColumnIndex(REVIEW_TEXT))
                    review = Review(id, userId, movieName, reviewText)

                    return review
                }
            } while (cursor.moveToNext())
        }
        return null
    }

    fun updateReview(review: Review): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, review.id)
        contentValues.put(USER_ID, review.userId)
        contentValues.put(MOVIE_NAME, review.movieName)
        contentValues.put(REVIEW_TEXT, review.text)

        val success = db.update(TBL_REVIEW, contentValues, "$ID=${review.id}", null)
        db.close()
        return success
    }

    fun deleteReviewById(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_REVIEW, "$ID=$id", null)
        db.close()
        return success
    }
}