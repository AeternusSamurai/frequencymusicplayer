package me.aeternussamurai.frequencymusicplayer;

import java.util.ArrayList;

import me.aeternussamurai.frequencymusicplayer.model.Song;

public class Playlist {
	private ArrayList<Song> list; //Play orders will be determined by how the list is read for saving
	private ArrayList<Long> songIDs;
	private long ID;
	private String name;
	private String path;
	
	public Playlist(long i, String n, String p){
		list = new ArrayList<Song>();
		songIDs = new ArrayList<Long>();
		ID = i;
		name = n;
		path = p;
	}
	
	public ArrayList<Song> getList(){
		return list;
	}
	
	public ArrayList<Long> getSongIDs(){
		return songIDs;
	}
	
	/**
	 * Add a song id to the playlist's song id container
	 * @param id The song id to add
	 */
	public void add(long id){
		songIDs.add(id);
	}
		
	public long getID(){
		return ID;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPath(){
		return path;
	}
	
	/**
	 * Add a song to the playlist
	 * @param s The song to add to the playList
	 */
	public void add(Song s){
		list.add(s);
	}
	
	public int size(){
		return list.size();
	}
	
	public long getTotalDuration(){
		long time = 0;
		for(Song s : list){
			time += s.getDuration();
		}
		return time;
	}
}
