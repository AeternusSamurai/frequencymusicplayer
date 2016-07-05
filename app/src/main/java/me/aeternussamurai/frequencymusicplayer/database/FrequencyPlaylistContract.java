package me.aeternussamurai.frequencymusicplayer.database;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Created by Chase on 5/23/2015.
 */
public final class FrequencyPlaylistContract {
    public FrequencyPlaylistContract(){
    }

    public static abstract class FrequencyPlaylists implements BaseColumns {

        public static final String TABLE_NAME = "frequencyplaylists";
        public static final String NAME = "name";
        public static final String NUM_SONGS = "num_songs";
        public static final String NUM_FREQ_SONGS = "num_freq_songs";

    }
}
