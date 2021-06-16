package com.chatter.classes;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Message {
    @Exclude
    private String key;
    private String textContent;
    private String senderEmail;
    private Date timestamp;
    private String mediaKey;
    private Boolean containsMedia = false;
    private Location location;
    private Boolean containsLocation = false;

    public Message(String mediaKey, Boolean containsMedia) {
        this.senderEmail = User.getEmail();
        this.timestamp = new Date();
        this.mediaKey = mediaKey;
        this.containsMedia = containsMedia;
    }
    public Message(String textContent) {
        this.senderEmail = User.getEmail();
        this.timestamp = new Date();
        this.textContent = textContent;
    }
    public Message(Location location) {
        this.senderEmail = User.getEmail();
        this.timestamp = new Date();
        this.location = location;
        this.containsLocation = true;
    }

    private Message() {
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getContainsMedia() {
        return containsMedia;
    }

    public void setContainsMedia(Boolean containsMedia) {
        this.containsMedia = containsMedia;
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getContainsLocation() {
        return containsLocation;
    }

    public void setContainsLocation(Boolean containsLocation) {
        this.containsLocation = containsLocation;
    }

}
