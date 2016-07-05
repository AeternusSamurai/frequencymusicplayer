package me.aeternussamurai.frequencymusicplayer.model;

import java.util.List;

/**
 * Created by Chase on 4/19/2016.
 */
public class FrequencyPlaylist implements PlaylistBase {

    private Long id;
    private String name;
    private Integer numSongs;
    private Integer numFregSongs;
    private List<FrequencySong> list;

    public FrequencyPlaylist(Long id, String name, Integer numSongs, Integer numFregSongs) {
        this.id = id;
        this.name = name;
        this.numSongs = numSongs;
        this.numFregSongs = numFregSongs;
    }

    // create constructor to upgrade a normal playlist to a frequency playlist.

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumSongs() {
        return numSongs;
    }

    public void setNumSongs(Integer numSongs) {
        this.numSongs = numSongs;
    }

    public Integer getNumFregSongs() {
        return numFregSongs;
    }

    public void setNumFregSongs(Integer numFregSongs) {
        this.numFregSongs = numFregSongs;
    }
}
