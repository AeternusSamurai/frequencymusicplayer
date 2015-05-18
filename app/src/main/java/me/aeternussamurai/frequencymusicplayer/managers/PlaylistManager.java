package me.aeternussamurai.frequencymusicplayer.managers;

import java.util.ArrayList;
import java.util.HashMap;

import me.aeternussamurai.frequencymusicplayer.Playlist;
import me.aeternussamurai.frequencymusicplayer.Song;

public class PlaylistManager extends Manager<String, Playlist, Playlist> {
	//private HashMap<String, Playlist> list;
	
	public PlaylistManager(){
		list = new HashMap<String, Playlist>();
	}
	
	@Override
	public HashMap<String, Playlist> getList() {
		return list;
	}

	@Override
	public ArrayList<String> getManagerTags() {
		return new ArrayList<String>(list.keySet());
	}

	@Override
	public Playlist getItemByTag(String tag) {
		return list.get(tag);
	}

	@Override
	public void add(Playlist s) {
		list.put(s.getName(), s);
	}
	
	/**
	 * Add a song to the playlist by name. ONLY APPLIES TO NEW SONGS GOING INTO THE PLAYLIST
	 * @param name The name of the playlist
	 * @param s The NEW song to add
	 */
	public void addToPlaylist(String name, Song s){
		list.get(name).add(s);
		list.get(name).add(s.getId());
	}
	
	//TODO overload the add method to add songs dynamically to the playlists
	/*
	 * Most likely have to search for all of the playlists that contain the id of the incoming song.
	 * Each playlist will have to have a container of the song ids that appear in the playlist.
	 */
	
	public void add(Song s){
		for(String name: list.keySet()){
			if(list.get(name).getSongIDs().contains(s.getId())){
				list.get(name).add(s);
			}
		}
	}

}
