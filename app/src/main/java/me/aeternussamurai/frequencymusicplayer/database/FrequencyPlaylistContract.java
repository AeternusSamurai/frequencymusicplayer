package me.aeternussamurai.frequencymusicplayer.database;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Created by Chase on 5/23/2015.
 */
public class FrequencyPlaylistContract {
    public FrequencyPlaylistContract(){
    }

    public static class FrequencyPlaylists implements BaseColumns, MediaStore.Audio.PlaylistsColumns{

        public static final String TABLE_NAME = "frequencyplaylists";
        public static final String IS_FREQUENCY = "is_frequency";

        public static final class Members implements MediaStore.Audio.AudioColumns{

            public static final String TABLE_NAME = "frequencyplaylists.members";
            public static final String _ID = "_id";
            public static final String IS_FREQUENCY = "is_frequency";
            public static final String PLAYLIST_ID = "playlist_id";

        }
    }
}
