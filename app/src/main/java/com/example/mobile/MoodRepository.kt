//package com.example.mobile
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//class MoodRepository(private val moodDao: MoodDao) {
//
//    // Insert a mood record
//    suspend fun insertMood(mood: Mood) {
//        moodDao.insert(mood)
//    }
//
//    // Retrieve moods for a specific user
//    suspend fun getMoodsForUser(userId: String): List<Mood> {
//        return moodDao.getUserMoods(userId)
//    }
//
//    // Delete moods for a specific user
//    suspend fun deleteUserMoods(userId: String) {
//        moodDao.deleteMoodsForUser(userId)
//    }
//}
//
