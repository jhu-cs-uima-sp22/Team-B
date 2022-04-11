package com.example.disco.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disco.R;
import com.example.disco.model.SongModel;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {
    // TODO: this is just an example, should be populated by spotify algorithm
    private SongModel[] songs;
    private Context ctx;
    private int numSongs;
    private final int NUM_SONGS = 10;

    
    public ViewPager2Adapter(Context ctx) {
        this.ctx = ctx;
        this.songs = new SongModel[NUM_SONGS];
        this.numSongs = 0;
    }

    public void addSong(SongModel song, int pos) {
            songs[pos] = song;
            numSongs++;
    }

    public void setSongs(SongModel[] songs) { this.songs = songs; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.single_song_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            SongModel song = songs[position];
            holder.title.setText(song.songTitle);
            holder.artist.setText(song.songArtist);
            holder.albumArt.setImageBitmap(song.albumArt);
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return this.numSongs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView images;
        TextView title;
        TextView artist;
        ImageView albumArt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            artist = itemView.findViewById(R.id.songArtist);
            albumArt = itemView.findViewById(R.id.coverImage);
        }
    }

}
