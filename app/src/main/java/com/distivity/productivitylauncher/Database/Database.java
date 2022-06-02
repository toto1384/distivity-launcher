package com.distivity.productivitylauncher.Database;


import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.distivity.productivitylauncher.Pojos.Todo;


@androidx.room.Database(entities =
        {Todo.class} , version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static Database sInstance;

    public static Database getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        Database.class, Database.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract Dao dao();

}

