package aeternussamurai.frequencymusicplayerv2.managers;

import java.util.ArrayList;
import java.util.HashMap;

import aeternussamurai.frequencymusicplayerv2.Song;

public class ArtistManager extends Manager<String, ArrayList<Song>, Song> {
	private HashMap<String, ArrayList<Song>> list;
	
	public ArtistManager(){
		list = new HashMap<String, ArrayList<Song>>();
	}
	
	@Override
	public HashMap<String, ArrayList<Song>> getList(){
		return list;
	}
	
	@Override
	public ArrayList<String> getManagerTags(){
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(list.keySet());
		return temp;
	}
	
	@Override
	public ArrayList<Song> getItemByTag(String artist){
		return list.get(artist);
	}

	@Override
	public void add(Song s) {
		String artist = s.getArtist();
		if(list.containsKey(artist) == true){
			list.get(artist).add(s);
		}else{
			list.put(artist, new ArrayList<Song>());
			list.get(artist).add(s);
		}
	}	
}
