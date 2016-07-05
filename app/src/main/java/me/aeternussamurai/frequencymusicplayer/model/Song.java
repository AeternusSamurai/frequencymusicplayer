package me.aeternussamurai.frequencymusicplayer.model;

public class Song {

	private long id;
	private String title;
	private String artist;
	private String album;
	private String path;
	private long duration;
	private int track;
	private String composer;
	private String genre;
	
	public Song(long songID, String songTitle, String songArtist, String songAlbum, String songPath, long d, int t, String c, String g)
	{
		id = songID;
		title = songTitle;
		artist = songArtist;
		album = songAlbum;
		path = songPath;
		duration = d;
		track = t;
		composer = c;
		genre = g;
	}
	
	public Song(Song s){
		this.id = s.id;
		this.title = s.title;
		this.artist = s.artist;
		this.album = s.album;
		this.path = s.path;
		this.duration = s.duration;
		this.track = s.track;
		this.composer = s.composer;
		this.genre = s.genre;
	}
	
	public long getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public String getPath() {
		return path;
	}
	
	public long getDuration(){
		return duration;
	}
	
	public int getTrack(){
		return track;
	}
	
	public String getComposer(){
		return composer;
	}
	
	public String getGenre(){
		return genre;
	}
	
	public void setGenre(String g){
		genre = g;
	}
	
}
