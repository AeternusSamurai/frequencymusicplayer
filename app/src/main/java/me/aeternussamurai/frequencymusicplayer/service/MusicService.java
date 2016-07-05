package me.aeternussamurai.frequencymusicplayer.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import me.aeternussamurai.frequencymusicplayer.MainActivity;
import me.aeternussamurai.frequencymusicplayer.R;
import me.aeternussamurai.frequencymusicplayer.model.Song;

public class MusicService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnCompletionListener {

	private static MusicService instance = null;
	// private final String ACTION_PLAY = "PLAY";
//	public static final String BROADCAST_ACTION = "darksamurai.frequencymusicplayer.uiupdate";
//	public static final String BROADCAST_MAIN = "darksamurai.frequencymusicplayer.mainUpdate";

	private final Handler handlerNP = new Handler();
	private final Handler handlerMain = new Handler();
	private Intent nowPlayingIntent;
	private Intent mainIntent;

	private MediaPlayer player;
	// The containers for the songs
	private int songPos;
	private int tabPos;
	private String songTitle = "";
	private static final int NOTIFY_ID = 1;
	private boolean shuffle = false;
	private Random rand;

	private Song playSong;
	private Song prevSong;
	private Song nextSong;

	// The lists used by the now playing activity
	private ArrayList<Song> nowplayingSongs;
	private ArrayList<Song> sortedNowPlayingSongs;
	private ArrayList<Song> randNowPlayingSongs;

	enum State {
		Retriving, Stopped, Preparing, Playing, Paused
	};

	public enum Repeat {
		REPEAT_OFF, REPEAT_ONE, REPEAT_ALL
	};

	State state = State.Retriving;
	Repeat repeatState = Repeat.REPEAT_OFF;

	@Override
	public void onCreate() {
		super.onCreate();
		// instance = this;
		rand = new Random();
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startID) {
		if (instance == null) {
			player = new MediaPlayer();
			instance = this;
			initMusicPlayer();
		}
		rand = new Random();
		handlerNP.removeCallbacks(sendNPUpdate);
		handlerMain.removeCallbacks(sendMainUpdate);
		return START_STICKY;
	}

	private Runnable sendNPUpdate = new Runnable() {

		@Override
		public void run() {
			updateNP();
		}
	};

	private Runnable sendMainUpdate = new Runnable() {

		@Override
		public void run() {
			updateMain();

		}
	};

	private void updateNP() {
		sendBroadcast(nowPlayingIntent);
	}

	private void updateMain() {
		sendBroadcast(mainIntent);
	}

	public void initMusicPlayer() {
		player.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	public void setTabPos(int tp) {
		tabPos = tp;
	}

	public void nowPlayingUpdate() {
		//nowPlayingIntent = new Intent(BROADCAST_ACTION);
		handlerNP.postDelayed(sendNPUpdate, 300);
	}

	public void mainUpdate() {
		//mainIntent = new Intent(BROADCAST_MAIN);
		handlerMain.postDelayed(sendMainUpdate, 50);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (player.getCurrentPosition() > 0) {
			mp.reset();
			playNext();
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mp.reset();
		return false;
	}

	// /
	// / The Player is ready to do stuff, so make it so
	// /
	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		state = State.Playing;
		//nowPlayingIntent = new Intent(BROADCAST_ACTION);
		//mainIntent = new Intent(BROADCAST_MAIN);
		//handlerNP.postDelayed(sendNPUpdate, 100);
		// handlerMain.postDelayed(sendMainUpdate, 200);
		Intent notIntent = new Intent(this, MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		builder.setContentIntent(pendInt).setSmallIcon(R.drawable.fmp_icon_v5)
				.setTicker(songTitle).setOngoing(true)
				.setContentTitle("Playing").setContentText(songTitle);

		Notification not = builder.build();

		startForeground(NOTIFY_ID, not);
	}

	public static MusicService getInstance() {
		return instance;
	}

	/**
	 * This method is first called when a song gets selected from the songs
	 * lists displayed by the UI. This method is called initially and then is
	 * not called again unless from a new selection.
	 * 
	 * @param songInd
	 *            the position of the song in the list
	 * @param tab
	 *            The tab from the main activity viewpager that is currently in
	 *            view. This is mandatory to use the tag to get song lists.
	 * @param tag
	 *            The tag that indicates the "name" of the song list to get. May
	 *            be null if coming from main song list
	 */
	public void playSong(int songInd, int tab, String tag) {
		player.reset();
		initMusicPlayer();
		tabPos = tab;
		songPos = songInd;
		//Add adjustments to use the new MusicManager
		switch (tabPos) {
		case 0:
			// From the main song list
			//playSong = songsManager.get(songInd);
			//sortedNowPlayingSongs = songsManager.getSongs();
			break;
		case 1:
			// From the artist tab
			//playSong = artistManager.getItemByTag(tag).get(songInd);
			//sortedNowPlayingSongs = artistManager.getItemByTag(tag);
			break;
		case 2:
			// From the album tab
			//playSong = albumManager.getItemByTag(tag).get(songInd);
			//sortedNowPlayingSongs = albumManager.getItemByTag(tag);
			break;
		case 3:
			// From the playlist tab
			//sortedNowPlayingSongs = playlistManager.getItemByTag(tag).getList();
			playSong = sortedNowPlayingSongs.get(songInd);
			break;
		}

		nowplayingSongs = new ArrayList<Song>(sortedNowPlayingSongs);

		if (shuffle == true) {
			randNowPlayingSongs = new ArrayList<Song>(sortedNowPlayingSongs);
			Collections.shuffle(randNowPlayingSongs);
			// swap the current song in the shuffled list with the first song in
			// the list
			int shuffledInd = randNowPlayingSongs.indexOf(playSong);
			Collections.swap(randNowPlayingSongs, 0, shuffledInd);
			nowplayingSongs = new ArrayList<Song>(randNowPlayingSongs);
			Collections.shuffle(randNowPlayingSongs);
			songPos = 0;
		}
		if (songPos == 0 && repeatState == Repeat.REPEAT_OFF) {
			// Beginning of List and no repeat
			prevSong = new Song(0, "Beginning of Song List", "None", "None",
					"", 0, 0, "None", "None");
		} else if (songPos == 0) {
			// Beginngin of list and repeat
			prevSong = nowplayingSongs.get(nowplayingSongs.size() - 1);
		} else {
			// Normal behavior
			prevSong = nowplayingSongs.get(songPos - 1);
		}
		if (songPos == nowplayingSongs.size() - 1
				&& repeatState == Repeat.REPEAT_OFF) {
			// End of list and no repeat
			nextSong = new Song(0, "End of Song List", "None", "None", "", 0,
					0, "None", "None");
		} else if (songPos == nowplayingSongs.size() - 1) {
			// End of list and repeat
			if (shuffle == false)
				nextSong = nowplayingSongs.get(0);
			else
				nextSong = randNowPlayingSongs.get(0);
		} else {
			// Normal behavior
			nextSong = nowplayingSongs.get(songPos + 1);
		}

		long currSong = playSong.getId();
		songTitle = playSong.getTitle();
		Uri trackURI = ContentUris.withAppendedId(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				currSong);
		try {
			player.setDataSource(getApplicationContext(), trackURI);
		} catch (Exception e) {
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}
		state = State.Preparing;
		player.prepareAsync();
	}

	/**
	 * Used for when the shuffle button is clicked during playback
	 */
	public void randomize() {
		randNowPlayingSongs = new ArrayList<Song>(sortedNowPlayingSongs);
		Collections.shuffle(randNowPlayingSongs);
		nowplayingSongs = new ArrayList<Song>(randNowPlayingSongs);
		Collections.shuffle(randNowPlayingSongs);
		int shuffleIndex = nowplayingSongs.indexOf(playSong);
		Collections.swap(nowplayingSongs, shuffleIndex, 0);
		songPos = 0;
		if (repeatState == Repeat.REPEAT_OFF) {
			prevSong = new Song(0, "Beginning of Song List", "None", "None",
					"", 0, 0, "None", "None");
		} else {
			prevSong = nowplayingSongs.get(nowplayingSongs.size() - 1);
		}
		if (songPos + 1 == nowplayingSongs.size()) {
			nextSong = new Song(0, "End of Song List", "None", "None", "", 0,
					0, "None", "None");
		} else {
			nextSong = nowplayingSongs.get(songPos + 1);

		}
		handlerNP.post(sendNPUpdate);
	}

	/**
	 * Also used for when the shuffle button is clicked during playback
	 */
	public void derandomize() {
		nowplayingSongs = sortedNowPlayingSongs;
		songPos = nowplayingSongs.indexOf(playSong);
		if (songPos == 0 && repeatState == Repeat.REPEAT_OFF) {
			prevSong = new Song(0, "Beginning of Song List", "None", "None",
					"", 0, 0, "None", "None");
		} else if (songPos == 0) {
			prevSong = nowplayingSongs.get(nowplayingSongs.size() - 1);
		} else {
			prevSong = nowplayingSongs.get(songPos - 1);
		}
		if (songPos == nowplayingSongs.size() - 1
				&& repeatState == Repeat.REPEAT_OFF) {
			nextSong = new Song(0, "End of Song List", "None", "None", "", 0,
					0, "None", "None");
		} else if (songPos == nowplayingSongs.size() - 1) {
			nextSong = nowplayingSongs.get(0);
		} else {
			nextSong = nowplayingSongs.get(songPos + 1);
		}
		handlerNP.post(sendNPUpdate);
	}

	public void playSong() {
		player.reset();
		initMusicPlayer();
		if (songPos == 0 && repeatState == Repeat.REPEAT_OFF) {
			// Beginning of list and no repeat
			prevSong = new Song(0, "Beginning of Song List", "None", "None",
					"", 0, 0, "None", "None");
		} else if (songPos == 0) {
			// Beginning of list and repeat
			prevSong = nowplayingSongs.get(nowplayingSongs.size() - 1);
		} else {
			// Normal behavior
			prevSong = nowplayingSongs.get(songPos - 1);
		}
		playSong = nowplayingSongs.get(songPos);
		if (songPos == nowplayingSongs.size() - 1
				&& repeatState == Repeat.REPEAT_OFF) {
			// End of list and no repeat
			nextSong = new Song(0, "End of Song List", "None", "None", "", 0,
					0, "None", "None");
		} else if (songPos == nowplayingSongs.size() - 1) {
			// End of list and repeat
			nextSong = randNowPlayingSongs.get(0);
		} else {
			// Normal Behavior
			nextSong = nowplayingSongs.get(songPos + 1);
		}

		long currSong = playSong.getId();
		songTitle = playSong.getTitle();
		Uri trackURI = ContentUris.withAppendedId(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				currSong);
		try {
			player.setDataSource(getApplicationContext(), trackURI);
		} catch (Exception e) {
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}
		state = State.Preparing;
		player.prepareAsync();
	}

	public Song getPrevSong() {
		return prevSong;
	}

	public Song getCurrSong() {
		return playSong;
	}

	public Song getNextSong() {
		return nextSong;
	}

	public void setSongPos(int songInd) {
		songPos = songInd;
	}

	public int getPos() {
		return player.getCurrentPosition();
	}

	public int getDurration() {
		return player.getDuration();
	}

	public boolean isPlaying() {
		if (state == State.Playing && player.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}

	public void pausePlayer() {
		player.pause();
		state = State.Paused;
	}

	public void seek(int pos) {
		player.seekTo(pos);
	}

	public void play() {
		player.start();
		state = State.Playing;
	}

	public void playPrev() {
		songPos--;
		if (songPos < 0 && repeatState == Repeat.REPEAT_OFF) {
			// Beginning of list and no repeat
			songPos = 0;
		} else if (songPos < 0 && repeatState == Repeat.REPEAT_ALL
				|| repeatState == Repeat.REPEAT_ONE) {
			// Beginning of list and repeat all or repeat one
			songPos = nowplayingSongs.size() - 1;
		}
		playSong();
	}

	public void forceNext() {
		songPos++;
		if (songPos >= nowplayingSongs.size()
				&& repeatState == Repeat.REPEAT_OFF) {
			playerReset();
		} else if (songPos >= nowplayingSongs.size()
				&& repeatState == Repeat.REPEAT_ALL
				|| repeatState == Repeat.REPEAT_ONE) {
			songPos = 0;
		}

	}

	private void playerReset() {
		player.stop();
		player.reset();
		initMusicPlayer();
		state = State.Retriving;
	}

	public void playNext() {
		songPos++;
		if (songPos >= nowplayingSongs.size()
				&& repeatState == Repeat.REPEAT_OFF) {
			playerReset();
		} else if (songPos >= nowplayingSongs.size()
				&& repeatState == Repeat.REPEAT_ALL) {
			songPos = 0;
			if (shuffle == true) {
				nowplayingSongs = new ArrayList<Song>(randNowPlayingSongs);
				Collections.shuffle(randNowPlayingSongs);
			}
		} else if (repeatState == Repeat.REPEAT_ONE) {
			songPos--;
		}
		playSong();
	}

	@Override
	public void onDestroy() {
		if (player != null && state != State.Playing) {
			stopForeground(true);
			player.release();
		}
		handlerNP.removeCallbacks(sendNPUpdate);
		handlerMain.removeCallbacks(sendMainUpdate);
		state = State.Retriving;
	}

    //TODO move to MusicManger
//	public void shuffle(int id) {
//		if (shuffle == true) {
//			shuffle = false;
//			if(id == R.id.shuffle_button){
//				derandomize();
//			}
//		} else {
//			shuffle = true;
//			if(id == R.id.shuffle_button){
//				randomize();
//			}
//		}
//	}

	public boolean getShuffle() {
		return shuffle;
	}

	public int getSongPos() {
		return songPos;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void changeRepeatState() {
		if (repeatState == Repeat.REPEAT_OFF) {
			repeatState = Repeat.REPEAT_ONE;
			if(songPos == 0){
				prevSong = nowplayingSongs.get(nowplayingSongs.size() - 1);
			}else{
				prevSong = nowplayingSongs.get(songPos - 1);
			}
			if(songPos == nowplayingSongs.size() - 1 && shuffle == false){
				nextSong = nowplayingSongs.get(0);
			}else if(songPos == nowplayingSongs.size() - 1 && shuffle == true){
				nextSong = randNowPlayingSongs.get(0);
			}else{
				nextSong = nowplayingSongs.get(songPos + 1);
			}
		} else if (repeatState == Repeat.REPEAT_ONE) {
			repeatState = Repeat.REPEAT_ALL;
			if(songPos == 0){
				prevSong = nowplayingSongs.get(nowplayingSongs.size() - 1);
			}else{
				prevSong = nowplayingSongs.get(songPos - 1);
			}
			if(songPos == nowplayingSongs.size() - 1 && shuffle == false){
				nextSong = nowplayingSongs.get(0);
			}else if(songPos == nowplayingSongs.size() - 1 && shuffle == true){
				nextSong = randNowPlayingSongs.get(0);
			}else{
				nextSong = nowplayingSongs.get(songPos + 1);
			}
		} else {
			repeatState = Repeat.REPEAT_OFF;
			if(songPos == 0){
				prevSong = new Song(0, "Beginning of Song List", "None", "None",
						"", 0, 0, "None", "None");
			}else{
				prevSong = nowplayingSongs.get(songPos - 1);
			}
			if(songPos == nowplayingSongs.size() - 1){
				nextSong = new Song(0, "End of Song List", "None", "None", "", 0,
						0, "None", "None");
			}else{
				nextSong = nowplayingSongs.get(songPos + 1);
			}
		}
		handlerNP.post(sendNPUpdate);
	}

	public Repeat getRepeatState() {
		return repeatState;
	}

	public ArrayList<Song> getNowPlaying() {
		return nowplayingSongs;
	}

}
