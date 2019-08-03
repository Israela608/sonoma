package com.lassafever.sonoma;

import android.graphics.Bitmap;

public class MediaDataAlbum {
    private String album;
    private String artist;
    private long albumId;
    private Bitmap albumArt;

    public MediaDataAlbum(String album, String artist, long albumId, Bitmap albumArt){
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.albumArt = albumArt;
    }


    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public Bitmap getAlbumArt(){
        return albumArt;
    }
}
