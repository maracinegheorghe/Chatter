package com.chatter.classes;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;

@Entity
public class Media {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "mediaLink")
    public String mediaLink;
    @ColumnInfo(name = "mediaType")
    public String mediaType;
    @ColumnInfo(name = "localPath")
    public String localPath;

    public Media(@NonNull String mediaLink, String mediaType, String localPath){
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
        this.localPath = localPath;
    }
}
