package com.chatter.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.chatter.classes.Media;

@Database(entities =  {Media.class}, version = 1)
public abstract  class ChatterDatabase extends RoomDatabase {
    public abstract MediaDAO mediaDAO();
}
