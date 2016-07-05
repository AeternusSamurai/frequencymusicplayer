package me.aeternussamurai.frequencymusicplayer.model;

import java.util.Date;

/**
 * Created by Chase on 4/19/2016.
 */
public class Playlist implements PlaylistBase {

    private Long id;
    private String data;
    private String name;
    private Date dateAdded;
    private Date dateModified;

    public Playlist(Long id, String data, String name, Date dateAdded, Date dateModified) {
        this.id = id;
        this.data = data;
        this.name = name;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

}
