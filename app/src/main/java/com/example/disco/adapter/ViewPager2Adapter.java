package com.example.disco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disco.MainActivity;
import com.example.disco.R;
import com.example.disco.model.SongModel;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {
    private SongModel[] songs;
    private Context ctx;
    private int numSongs;
    private final int NUM_SONGS = 13;

    
    public ViewPager2Adapter(Context ctx) {
        this.ctx = ctx;
        this.songs = new SongModel[NUM_SONGS];
        this.numSongs = 0;
    }

    /**
     * Add a new song to our adapter
     * @param song Song Information
     * @param pos  Position of the song in the array
     */
    public void addSong(SongModel song, int pos) {
        songs[pos] = song;
        numSongs++;
    }

    /**
     * Delete all songs from the adapter
     */
    public void clearSongs() {
        this.songs = new SongModel[NUM_SONGS];
        this.numSongs = 0;
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

        TextView title;
        TextView artist;
        ImageView albumArt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            title.setSelected(true);
            artist = itemView.findViewById(R.id.songArtist);
            artist.setSelected(true);
            albumArt = itemView.findViewById(R.id.coverImage);
        }

    }

}
