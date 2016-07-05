package me.aeternussamurai.frequencymusicplayer.database;

import android.provider.BaseColumns;

/**
 * Created by Chase on 6/23/2016.
 */
public class FrequencySongContract {
    public FrequencySongContract() {
    }

    public static abstract class FrequencySongs implements BaseColumns {
        public static final String TABLE_NAME = "frequencysongs";
        public static final String MEDIA_ID = "media_id";
        public static final String FREQUENCY = "frequency";
        public static final String FREQ_PLAY_ID = "freq_play_id";
    }
}
