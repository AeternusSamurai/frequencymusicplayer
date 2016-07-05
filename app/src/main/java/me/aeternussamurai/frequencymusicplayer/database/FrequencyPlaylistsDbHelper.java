package me.aeternussamurai.frequencymusicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import me.aeternussamurai.frequencymusicplayer.model.FrequencyPlaylist;


/**
 * Created by Chase on 5/24/2015.
 */
public class FrequencyPlaylistsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FrequencyPlaylists.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String CREATE_FREQUENCY_PLAYLISTS =
            "CREATE TABLE " + FrequencyPlaylistContract.FrequencyPlaylists.TABLE_NAME + " (" +
                    FrequencyPlaylistContract.FrequencyPlaylists._ID + " INTEGER PRIMARY KEY," +
                    FrequencyPlaylistContract.FrequencyPlaylists.NAME + TEXT_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists.NUM_SONGS + INTEGER_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists.NUM_FREQ_SONGS + INTEGER_TYPE +
                    " )";
    private static final String CREATE_FREQUENCY_SONGS =
            "CREATE TABLE " + FrequencySongContract.FrequencySongs.TABLE_NAME + " (" +
                    FrequencySongContract.FrequencySongs._ID + " INTEGER PRIMARY KEY," +
                    FrequencySongContract.FrequencySongs.MEDIA_ID + INTEGER_TYPE + COMMA_SEP +
                    FrequencySongContract.FrequencySongs.FREQUENCY + INTEGER_TYPE + COMMA_SEP +
                    FrequencySongContract.FrequencySongs.FREQ_PLAY_ID + INTEGER_TYPE +
                    " )";
    private static final String DELETE_FREQUENCY_PLAYLISTS =
            "DROP TABLE IF EXISTS " + FrequencyPlaylistContract.FrequencyPlaylists.TABLE_NAME;
    private static final String DELETE_FREQUENCY_SONGS =
            "DROP TALBE IF EXISTS " + FrequencySongContract.FrequencySongs.TABLE_NAME;


    public FrequencyPlaylistsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FREQUENCY_PLAYLISTS);
        db.execSQL(CREATE_FREQUENCY_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Make changes to the database tables if needed. Changes include adding rows, renaming rows, renaming tables, etc.
        // Refer to using alter table to help make the necessary changes.
    }

    public void addNewFrequencyPlaylist(FrequencyPlaylist fpl) {
    }
}
