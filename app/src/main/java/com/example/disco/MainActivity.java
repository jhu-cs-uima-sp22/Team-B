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

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.disco.adapter.SongDBAdapter;
import com.example.disco.adapter.ViewPager2Adapter;
import com.example.disco.model.SongModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // this is the swiping class
    private ViewPager2 viewPager2;
    private ViewPager2Adapter vp2a;
    private Spinner spinner;
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

    // Initial PlayList will be the "Recently Played Music" Playlist
    private static String PLAYLIST_URI = "spotify:playlist:37i9dQZF1DWW2mn5wEfG6q";
    private static final int NUM_SONGS = 10;

    private static SpotifyAppRemote mSpotifyAppRemote;
    private static ListItem[] recommendedContent;
    public static List<String> spotifyGenres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SongDBAdapter sdba = new SongDBAdapter(this);
        dbrefW = sdba.getWritableDatabase();
        dbrefR = sdba.getReadableDatabase();

        connectSpotify();
    }

    private void connectSpotify() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,new Connector.ConnectionListener() {
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;

                //  Get the recommended content from Spotify
                getRecommendedContent();
            }

            public void onFailure(Throwable throwable) {
                // Something went wrong when attempting to connect! Handle errors here
            }
        });
    }

    /**
     * Fetch all recommended Content from Spotify
     */
    private void getRecommendedContent() {
        mSpotifyAppRemote.getContentApi().getRecommendedContentItems("")
                .setResultCallback(list -> {
                    recommendedContent = list.items;
                    for(ListItem elem: list.items){
                        spotifyGenres.add(elem.title);
                    }

                    // Only set up the view when the information is fetched
                    setUpSpinner();
                    setUpViewPage();

                    // Now you can start interacting with App Remote
                    connected();
                });
    }

    /**
     * Configure and Fill the dropDown Menu with all possible genres
     */
    private void setUpSpinner(){
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.spotifyGenres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String genre = adapterView.getItemAtPosition(i).toString();
                getMusicGenre(genre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Set up the viewPage
     */
    private void setUpViewPage() {
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

    private void connected() {
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        if(songsLoaded < NUM_SONGS) {
                            getSongAt(songsLoaded);
                            songsLoaded++;
                        }
                    }
                });

        //  After connecting to spotify, Get the songs in playlist
        getAllSongs();

        playlistStarted = true;
    }

    /**
     * Fetch all songs in the playlist PLAYLIST_URI
     */
    private void getAllSongs() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play(PLAYLIST_URI);

        vp2a.clearSongs();
        for (int i = 0; i < NUM_SONGS; i++) {
            getSongAt(i);
        }
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


    /**
     * Fetch the playlist for the specific genre
     * @param genre Music genre from Spotify
     */
    private void getMusicGenre(String genre) {
        ListItem itemSelected = null;

        for (ListItem elem : recommendedContent) {
            //  Get the item selected by user
            if (elem.title.equals(genre)) {
                itemSelected = elem;
                break;
            }
        }

        //  Fetch the playlist URI
        mSpotifyAppRemote.getContentApi().getChildrenOfItem(itemSelected, 1, 0)
            .setResultCallback(e -> {
                PLAYLIST_URI = e.items[0].uri;
                getAllSongs();
            });
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
                    startActivity(sendIntent);
                });
    }

    public SongModel[] getAllLikedSongs() {
        SongModel[] songs = new SongModel[numLikedSongs()];
        Cursor cursor = dbrefR.query("LikedSongs", null, null, null, null, null , null);
        int i = 0;
        while (cursor.moveToNext()) {
            songs[i] = new SongModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            i++;
        }
        return songs;
    }

    public boolean hasLikedSong(String songURL) {
        Cursor cursor = dbrefR.query("LikedSongs", null, "songURL=\"" + songURL + "\"", null, null, null , null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public int numLikedSongs() {
        Cursor cursor = dbrefR.query("LikedSongs", null, null, null, null, null , null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        return i;
    }

    public void likeSong(View view) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                    Track track = playerState.track;
                    if (hasLikedSong(track.uri)) {
                        //Unlike - remove song from DB
                        dbrefW.execSQL("DELETE FROM LikedSongs WHERE songURL=\"" + track.uri + "\"");
                        Toast.makeText(this, "Removed from your liked songs.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Like - add song to DB
                        dbrefW.execSQL("INSERT INTO LikedSongs " +
                                "(songName, songArtist, songAlbum, songURL, songAlbumURI)" +
                                "VALUES (\"" + track.name + "\",\"" + track.artist.name
                                + "\",\"" + track.album.name + "\",\"" + track.uri + "\",\"" + track.imageUri.raw + "\")"
                        );
                        Toast.makeText(this, "Added to your liked songs!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}