package me.aeternussamurai.frequencymusicplayer.model;

/**
 * Created by Chase on 6/23/2016.
 */
public class FrequencySong extends Song {

    private Frequency frequency;
    private Long id;

    public FrequencySong(Long id, long songID, String songTitle, String songArtist, String songAlbum, String songPath, long d, int t, String c, String g, Frequency frequency) {
        super(songID, songTitle, songArtist, songAlbum, songPath, d, t, c, g);
        this.id = id;
        this.frequency = frequency;
    }
}
