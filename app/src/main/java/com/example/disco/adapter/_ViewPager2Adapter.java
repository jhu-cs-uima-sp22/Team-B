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

public class _ViewPager2Adapter extends RecyclerView.Adapter<_ViewPager2Adapter.ViewHolder> {
    private SongModel[] songs;
    private Context ctx;
    private int numSongs;
    private final int NUM_SONGS = 10;


    public _ViewPager2Adapter(Context ctx) {
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
        Spinner spinner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            title.setSelected(true);
            artist = itemView.findViewById(R.id.songArtist);
            artist.setSelected(true);
            albumArt = itemView.findViewById(R.id.coverImage);

            //setUpSpinner(itemView);
        }


/*
        private void setUpSpinner(View itemView){
            spinner = itemView.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(itemView.getContext(), R.array.genres, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String genre = adapterView.getItemAtPosition(i).toString();
                    MainActivity main = new MainActivity();
                    //main.getMusicGenre(genre);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

 */
    }

}
