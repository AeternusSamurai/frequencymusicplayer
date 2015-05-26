package me.aeternussamurai.frequencymusicplayer.managers;

import java.util.ArrayList;
import java.util.HashMap;

import me.aeternussamurai.frequencymusicplayer.Playlist;
import me.aeternussamurai.frequencymusicplayer.Song;

public interface Manager {

	public ArrayList<Song> getSongs();
	
	public ArrayList<Song> getSongsByAlbum(String album);

	public ArrayList<Song> getSongsByArtist(String artist);

	public Playlist getSongsByPlaylist(String playlist);

	public void addSong(Song song);

	public void addPlaylist(Playlist playlist);

    public void updatePlaylist(Playlist playlist);
	
}
