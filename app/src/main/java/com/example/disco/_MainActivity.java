package com.example.disco;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.disco.adapter.ViewPager2Adapter;
import com.example.disco.model.SongModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class _MainActivity extends AppCompatActivity {

    // this is the swiping class
    private ViewPager2 viewPager2;
    private ViewPager2Adapter vp2a;
    private boolean isPaused = false;
    private boolean playlistStarted = false;


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
        setContentView(R.layout._activity_main);
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
                //getMusicGenre();
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
            getSongAt(i);
        }
        playlistStarted = true;
    }

    public void getMusicGenre(String genre) {
        Log.d("TAG", "onItemSelected spinner : " + genre);
        //mSpotifyAppRemote.getPlayerApi().pause();
/*
        mSpotifyAppRemote.getContentApi().getRecommendedContentItems(genre)
            .setResultCallback(list -> {
                Log.d("TAG", "Return from spotify " + list.toString());
                Log.d("TAG", "Return from spotify items " + list.items[12]);

                for(ListItem elem : list.items){
                    Log.d("TAG", "GENRE " + elem.title);
                }

                mSpotifyAppRemote.getContentApi().getChildrenOfItem(list.items[12], 10, 0)
                    .setResultCallback(e -> {
                        Log.d("TAG", "Return from get children of item  " + e.toString());
                        Log.d("TAG", "Return from get children of item getItem " + e.items[0]);
                        //mSpotifyAppRemote.getContentApi().playContentItem(e.items[0]);
                        mSpotifyAppRemote.getPlayerApi().play(e.items[0].uri);
                    });
            });

 */
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
                    sendIntent.putExtra(Intent.EXTRA_TEXT, songLink);
                    sendIntent.setType("text/plain");
                    Intent.createChooser(sendIntent,"Share via...");
                    startActivity(sendIntent);
                });
    }

}