package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchLocation::class, MyCity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val searchDAO : SearchDAO
    abstract val myCitiesDAO: MyCitiesDAO

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null
        fun getInstance(context: Context):AppDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()
                }
                return instance
            }
        }

    }
}