package com.example.disco.model;
import android.graphics.Bitmap;

import com.spotify.protocol.types.Track;

public class SongModel {

    public String songTitle;
    public String songArtist;
    public String songAlbum;
    public String songURL;
    public Bitmap albumArt;


    public SongModel(String songTitle, String songArtist, String songAlbum, String songURL) {
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songURL = songURL;
    }

    public SongModel(String songTitle, String songArtist) {
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songAlbum = "";
        this.songURL = "";
    }

    public SongModel(Track track, Bitmap bmp) {
        this.songTitle = track.name;
        this.songArtist = track.artist.name;
        this.songAlbum = track.album.name;
        this.songURL = track.uri;
        this.albumArt = bmp;

    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) { return false; }
        SongModel other = (SongModel) o;
        return this.songTitle.equals(other.songTitle) && this.songArtist.equals(other.songArtist);
    }

}
