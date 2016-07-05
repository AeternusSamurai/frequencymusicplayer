package me.aeternussamurai.frequencymusicplayer.managers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

import me.aeternussamurai.frequencymusicplayer.model.Song;

/**
 * Created by Chase on 2/26/2015.
 */
public class MusicManager {

    // Variables for lists currently playing
    private ArrayList<Long> nowPlaying;
    private ArrayList<Long> sortedNowPlaying;
    private ArrayList<Long> nextNowPlaying;

    // Variables for the NowPlaying screen
    private Song current;
    private Song previous;
    private Song next;

    // Variables for pre-loading songs from selection


    // Variables for loading playlists


    private ContentResolver contentResolver;

    public MusicManager(ContentResolver cr) {

        contentResolver = cr;
    }

}
