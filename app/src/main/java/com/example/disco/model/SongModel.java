package com.example.disco.model;

public class SongModel {

    public String songTitle;
    public String songArtist;
    public String songAlbum;
    public String songURL;


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

}
