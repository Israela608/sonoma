package com.lassafever.sonoma;

import android.graphics.Bitmap;

public class MediaDataArtist {
    private String artist;
    private Bitmap albumArt;
    private int songSum;

    public MediaDataArtist(String artist, Bitmap albumArt, int songSum){
        this.artist = artist;
        this.albumArt = albumArt;
        this.songSum = songSum;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getAlbumArt(){
        return albumArt;
    }

    public int getSongSum(){
        return songSum;
    }
}
