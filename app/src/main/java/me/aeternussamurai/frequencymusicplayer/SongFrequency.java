package me.aeternussamurai.frequencymusicplayer;

/**
 * Created by Chase on 5/23/2015.
 */
public class SongFrequency {
    private long song_id;
    private long playlist_id;
    private Frequency frequency;

    public long getSong_id() {
        return song_id;
    }

    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }

    public long getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(long playlist_id) {
        this.playlist_id = playlist_id;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
}
