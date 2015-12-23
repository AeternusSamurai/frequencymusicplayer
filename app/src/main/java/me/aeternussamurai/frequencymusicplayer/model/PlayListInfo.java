package me.aeternussamurai.frequencymusicplayer.model;

/**
 * Created by Chase on 11/20/2015.
 */
public class PlayListInfo {
    private long ID;
    private String name;
    private String dataPath;

    public PlayListInfo(){

    }

    public PlayListInfo(long ID, String name, String dataPath){
        this.ID = ID;
        this.name = name;
        this.dataPath = dataPath;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(ID).hashCode();
    }
}
