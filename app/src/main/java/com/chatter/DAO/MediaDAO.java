package com.chatter.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chatter.classes.Media;

import java.util.List;

@Dao
public interface MediaDAO {
    @Query("SELECT * FROM media")
    List<Media> getAll();

    @Query("SELECT * FROM media WHERE mediaLink = (:mediaLink)")
    Media getByLink(String mediaLink);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMedia(Media media);

    @Query("DELETE FROM media WHERE mediaLink = (:mediaLink)")
    void deleteMediaByLink(String mediaLink);
}
