package com.example.disco;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.disco.adapter.SongDBAdapter;
import com.example.disco.adapter.ViewPager2Adapter;
import com.example.disco.model.SongModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // this is the swiping class
    private ViewPager2 viewPager2;
    private ViewPager2Adapter vp2a;
    private SQLiteDatabase dbrefW;
    private SQLiteDatabase dbrefR;
    private boolean isPaused = false;
    private boolean playlistStarted = false;
    private int songsLoaded = 0;


    //TODO: these 2 lines are specific to the thing you create on your dashboard
    //https://developer.spotify.com/dashboard/applications
    //All other spotify code and packages SHOULD BE (I think/hope) installed and in the right place
    //Dependencies in build.gradle
    private static final String CLIENT_ID = "c6b8e2f8fb0e4ed8b2f03b863c28d07e";
    private static final String REDIRECT_URI = "com.example.disco://callback";
    //Can't get authorization to work, which has to do with my dashboard and these 2 lines
    //but I cannot figure out how to get it to work
    private static final String PLAYLIST_URI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL";
    private static final int NUM_SONGS = 10;

    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SongDBAdapter sdba = new SongDBAdapter(this);
        dbrefW = sdba.getWritableDatabase();
        dbrefR = sdba.getReadableDatabase();
        viewPager2 = findViewById(R.id.viewpager);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        vp2a = viewPager2Adapter;
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("MainActivity", "WHAT IS HAPPENING");
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("MainActivity", "PAGE SELECTED!");
                super.onPageSelected(position);
                if (playlistStarted) {
                    mSpotifyAppRemote.getPlayerApi().skipToIndex(PLAYLIST_URI, position);
                    isPaused = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,new Connector.ConnectionListener() {
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;

                // Now you can start interacting with App Remote
                connected();

            }

            public void onFailure(Throwable throwable) {
                // Something went wrong when attempting to connect! Handle errors here
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    /**
     * Get the current song playing
     * @param pos position of the song
     */
    private void getCurrentSong(int pos) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                        mSpotifyAppRemote.getImagesApi().getImage(playerState.track.imageUri)
                                .setResultCallback(img -> {
                                    vp2a.addSong(new SongModel(playerState.track, img), pos);
                                    viewPager2.setAdapter(vp2a);
                                });
                })
                .setErrorCallback(throwable -> {
                    Log.e("MainActivity", throwable.getMessage());
                });
    }


    /**
     * Get the song from Spotify at the specific position
     * @param pos Position of the song
     */
    private void getSongAt(int pos) {
        mSpotifyAppRemote.getPlayerApi().skipToIndex(PLAYLIST_URI, pos)
                .setResultCallback(empty -> {
                    getCurrentSong(pos);
                });
        if (pos == NUM_SONGS - 2) {
            mSpotifyAppRemote.getPlayerApi().skipToIndex(PLAYLIST_URI, 0);
        }
    }

    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play(PLAYLIST_URI);

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", "CONNECTED: " + track.name + " by " + track.artist.name);
                        if(songsLoaded < NUM_SONGS) {
                            getSongAt(songsLoaded);
                            songsLoaded++;
                        }
                    }
                });
        playlistStarted = true;
    }

    /**
     * Pause/Resume a song
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void togglePause(View view) {
        Context context = getApplicationContext();
        if (!isPaused) {
            mSpotifyAppRemote.getPlayerApi().pause();
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.play));
            // ((ImageButton) view).setForeground(ContextCompat.getDrawable(context, R.drawable.play));
            isPaused = true;
        } else {
            mSpotifyAppRemote.getPlayerApi().resume();
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.pause));
            // ((ImageButton) view).setForeground(ContextCompat.getDrawable(context, R.drawable.pause));
            isPaused = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void shareClicked(View view) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                   String songLink = "open.spotify.com/track/" + playerState.track.uri.substring(14);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool song I found on Disco, " +
                            "the music discovery app! " + songLink);
                    sendIntent.setType("text/plain");
                    Intent.createChooser(sendIntent,"Share via...");
                    Log.d("MainActivity", getLikedSongAt(0).songTitle);
                    startActivity(sendIntent);
                });
    }

    public SongModel getLikedSongAt(int pos) {
        Cursor cursor = dbrefR.query("LikedSongs", null, null, null, null, null , null);
        int i = 0;
        while(cursor.moveToNext()) {
            if (i == pos) {
                return new SongModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            }
        }
        return new SongModel("", "");
    }

    private int numLikedSongs = 0;
    public void likeSong(View view) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                    Track track = playerState.track;
                    dbrefW.execSQL("INSERT INTO LikedSongs " +
                            "(id, songName, songArtist, songAlbum, songURL, songAlbumURI)" +
                            "VALUES (" + numLikedSongs + ",\"" + track.name + "\",\"" + track.artist.name
                            + "\",\"" + track.album.name + "\",\"" + track.uri + "\",\"" + track.imageUri.raw + "\")"
                    );
                    numLikedSongs++;
                });
    }
}