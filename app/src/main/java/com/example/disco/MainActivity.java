package com.example.disco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.disco.adapter.ViewPager2Adapter;
import com.example.disco.model.SongModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    // this is the swiping class
    private ViewPager2 viewPager2;
    private ViewPager2Adapter vp2a;
    private boolean isPaused = false;
    private boolean playlistStarted = false;
    private ArrayList<Integer> song_order = new ArrayList<Integer>();
    private boolean currSongExists = false;


    //TODO: these 2 lines are specific to the thing you create on your dashboard
    //https://developer.spotify.com/dashboard/applications
    //All other spotify code and packages SHOULD BE (I think/hope) installed and in the right place
    //Dependencies in build.gradle
    private static final String CLIENT_ID = "c6b8e2f8fb0e4ed8b2f03b863c28d07e";
    private static final String REDIRECT_URI = "com.example.disco://callback";
    //Can't get authorization to work, which has to do with my dashboard and these 2 lines
    //but I cannot figure out how to get it to work
    private static final String PLAYLIST_URI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL";
    private static final int NUM_SONGS = 20;

    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.viewpager);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        vp2a = viewPager2Adapter;
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (playlistStarted) {
                    if (vp2a.getItemCount() < NUM_SONGS) {
                        //getNextSong();
                    }
                    mSpotifyAppRemote.getPlayerApi().skipToIndex(PLAYLIST_URI, song_order.get(position));
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

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        createSongOrder(NUM_SONGS);
                        System.out.println(song_order);
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }


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

    /*private int getPlaylistLength() {
        AtomicInteger curr = new AtomicInteger();
        AtomicInteger prev = new AtomicInteger();
        while(curr.get() != prev.get()) {
            prev.set(); = curr;
            mSpotifyAppRemote.getPlayerApi().skipToIndex(PLAYLIST_URI, curr.get())
                    .setResultCallback(empty -> {
                        curr.getAndIncrement();
                    });

        }
        return curr.get();
    }*/

    private void createSongOrder(int playlist_length) {
        for (int i = 0; i < playlist_length; i++) {
            song_order.add(i);
        }
        Collections.shuffle(song_order);
    }

    private void checkSongExists() {
        currSongExists = true;
    }

    private void getSongAt(int song_num, int pos) {
        System.out.println("songExists BEFORE: " + currSongExists);
        Log.d("MainActivity", vp2a.getItemCount() + "");
        mSpotifyAppRemote.getPlayerApi().skipToIndex(PLAYLIST_URI, song_num)
                .setResultCallback(empty -> {
                    checkSongExists();
                    getCurrentSong(pos);
                });
        System.out.println("songExists AFTER: " + currSongExists);
        if (pos == NUM_SONGS - 1) {
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
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
        for (int i = 0; i < NUM_SONGS; i++) {
            System.out.println(song_order.get(i));
            getSongAt(song_order.get(i), i);
        }
        playlistStarted = true;
    }

    public void go(View view) {
        Intent intent = new Intent(this, SingleSongRow.class);
        startActivity(intent);
    }

    public void togglePause(View view) {
        if (!isPaused) {
            mSpotifyAppRemote.getPlayerApi().pause();
            String s = "resume";
            ((Button) view).setText(s);
            isPaused = true;
        } else {
            mSpotifyAppRemote.getPlayerApi().resume();
            String s = "pause";
            ((Button) view).setText(s);
            isPaused = false;
        }
    }

}