package me.aeternussamurai.frequencymusicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import me.aeternussamurai.frequencymusicplayer.Frequency;

/**
 * Created by Chase on 5/24/2015.
 */
public class FrequencyPlaylistsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSIOM = 1;
    public static final String DATABASE_NAME = "FrequencyPlaylists.db";

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";
    public static final String CREATE_FREQPLAYLIST =
            "CREATE TABLE IF NOT EXISTS " + FrequencyPlaylistContract.FrequencyPlaylists.TABLE_NAME +
                    " (" + FrequencyPlaylistContract.FrequencyPlaylists._ID + " INTEGER PRIMARY KEY," +
                    FrequencyPlaylistContract.FrequencyPlaylists.NAME + TEXT_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists.DATE_ADDED + INTEGER_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists.DATE_MODIFIED + INTEGER_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists._COUNT + INTEGER_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists.IS_FREQUENCY + INTEGER_TYPE + COMMA_SEP +
                    FrequencyPlaylistContract.FrequencyPlaylists.DATA + TEXT_TYPE + " )";
//    public static final String CREATE_FREQMEMBERS =
//            "CREATE TABLE IF NOT EXISTS " + FrequencyPlaylistContract.FrequencyPlaylists.Members.TABLE_NAME +
//                    " (" + FrequencyPlaylistContract.FrequencyPlaylists.Members._ID + INTEGER_TYPE + "PRIMARY KEY," +
//                    FrequencyPlaylistContract.FrequencyPlaylists.Members.TITLE + TEXT_TYPE + COMMA_SEP +
//                    FrequencyPlaylistContract.FrequencyPlaylists.Members.TRACK + INTEGER_TYPE + COMMA_SEP +
//                    FrequencyPlaylistContract.FrequencyPlaylists.Members.ARTIST + TEXT_TYPE + COMMA_SEP +
//                    FrequencyPlaylistContract.FrequencyPlaylists.Members.ALBUM + TEXT_TYPE + COMMA_SEP +



    public FrequencyPlaylistsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSIOM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
