package com.chatter.classes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Media implements Parcelable {
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

    protected Media(Parcel in) {
        mediaLink = in.readString();
        mediaType = in.readString();
        localPath = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaLink);
        dest.writeString(mediaType);
        dest.writeString(localPath);
    }
}
