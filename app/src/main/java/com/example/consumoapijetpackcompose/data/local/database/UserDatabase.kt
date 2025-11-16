package com.example.consumoapijetpackcompose.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.consumoapijetpackcompose.data.local.dao.UserDao
import com.example.consumoapijetpackcompose.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}