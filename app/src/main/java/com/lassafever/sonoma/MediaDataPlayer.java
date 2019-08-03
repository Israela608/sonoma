package com.lassafever.sonoma;

import android.graphics.Bitmap;

public class MediaDataPlayer {
    private String title;
    private String artist;
    private String album;
    private String path;
    private long duration;
    private long albumId;
    private  String composer;
    private Bitmap albumArt;

    public MediaDataPlayer(String title, String artist, String album, String path, long duration, long albumId, String composer, Bitmap albumArt){
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.path = path;
        this.duration = duration;
        this.albumId = albumId;
        this.composer = composer;
        this.albumArt = albumArt;
    }


    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getComposer() {
        return composer;
    }

    public Bitmap getAlbumArt(){
        return albumArt;
    }
}
