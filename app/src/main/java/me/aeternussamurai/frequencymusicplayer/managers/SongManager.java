package me.aeternussamurai.frequencymusicplayer.managers;

import java.util.ArrayList;
import java.util.HashMap;

import me.aeternussamurai.frequencymusicplayer.Song;

public class SongManager extends Manager<Long, Song, Song> {

	ArrayList<Song> indexedSongs;
	HashMap<Long, Song> songsByID;

	public SongManager(){
		indexedSongs = new ArrayList<Song>();
		songsByID = new HashMap<Long, Song>();
	}

	@Override
	public HashMap<Long, Song> getList() {
		return songsByID;
	}

	@Override
	public ArrayList<Long> getManagerTags() {
		return new ArrayList<Long>(songsByID.keySet());
	}

	@Override
	public Song getItemByTag(Long tag) {
		return songsByID.get(tag);
	}

	@Override
	public void add(Song s) {
		indexedSongs.add(s);
		songsByID.put(s.getId(), s);		
	}

	@Override
	public boolean isEmpty() {
		if(indexedSongs.isEmpty() && songsByID.isEmpty())
			return true;
		return false;
	}
	
	public ArrayList<Song> getSongs(){
		return indexedSongs;
	}
	
	public Song get(int index){
		return indexedSongs.get(index);
	}
}
