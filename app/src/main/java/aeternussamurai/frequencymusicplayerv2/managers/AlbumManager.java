package aeternussamurai.frequencymusicplayerv2.managers;

import java.util.ArrayList;
import java.util.HashMap;

import aeternussamurai.frequencymusicplayerv2.Song;

public class AlbumManager extends Manager<String, ArrayList<Song>, Song> {
	private HashMap<String, ArrayList<Song>> list;
	
	public AlbumManager(){
		list = new HashMap<String, ArrayList<Song>>();
	}
	
	public HashMap<String, ArrayList<Song>> getList(){
		return list;
	}
	
	public ArrayList<String> getManagerTags(){
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(list.keySet());
		return temp;
	}
	
	public ArrayList<Song> getItemByTag(String al){
		return list.get(al);
	}
	
	public String getFirstSongPath(String al){
		return list.get(al).get(0).getPath();
	}

	public void add(Song s) {
		String album = s.getAlbum();
		if(list.containsKey(album) == true){
			list.get(album).add(s);
		}else{
			list.put(album, new ArrayList<Song>());
			list.get(album).add(s);
		}
	}

}
