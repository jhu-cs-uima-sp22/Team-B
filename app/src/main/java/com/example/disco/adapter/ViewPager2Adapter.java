package com.example.disco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disco.R;
import com.example.disco.model.SongModel;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {

    // TODO: this is just an example, should be populated by spotify algorithm
    private SongModel[] songs = {
            new SongModel("Animal", "Neon Trees"),
            new SongModel("Check Yes, Juliet", "We The Kings"),
            new SongModel("Fire Burning", "Sean Kingston")
    };

    private Context ctx;
    public ViewPager2Adapter(Context ctx) {
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.single_song_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongModel song = songs[position];
        holder.title.setText(song.songTitle);
        holder.artist.setText(song.songArtist);
        //holder.images.setImageResource(songs[position]);
    }

    @Override
    public int getItemCount() {
        return songs.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView images;
        TextView title;
        TextView artist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //images = itemView.findViewById(R.id.background);
            title = itemView.findViewById(R.id.songTitle);
            artist = itemView.findViewById(R.id.songArtist);
        }
    }

}
