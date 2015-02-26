package aeternussamurai.frequencymusicplayerv2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import aeternussamurai.frequencymusicplayerv2.R;

/**
 * The Main Activity of the Frequency Music Player. 
 * @author Chase Parks
 * Created June 6, 2014
 */
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener{

	private ViewPager vp;
	private ActionBar actionBar;
	private Intent playIntent;

	//private Intent nowPlayingIntent;

	private int tabPos;
	private int songPos;

	private ToggleButton playPauseButton;

	private LinearLayout currentSongLay;

	private BroadcastReceiver receiver;
	//private Intent selectionIntent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onStart() {
	}

	@Override
	protected void onResume() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			// settings
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		vp.setCurrentItem(tab.getPosition());
		tabPos = tab.getPosition();

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

//	/**
//	 * This is a helper method to set the data of the current song
//	 * to the UI. The current playing song must not be null.
//	 */
//	private void setCurrentSong() {
//		currentSongLay.setEnabled(true);
//		currentSongLay.setAlpha(1);
//		MediaMetadataRetriever meta = new MediaMetadataRetriever();
//		Song currSong = musicSer.getCurrSong();
//		Uri currUri = Uri.fromFile(new File(currSong.getPath()));
//		ImageView currImage = (ImageView) findViewById(R.id.main_current_image);
//		TextView currTitle = (TextView) findViewById(R.id.main_current_title);
//		TextView currArtist = (TextView) findViewById(R.id.main_current_artist);
//		try {
//			meta.setDataSource(this, currUri);
//		} catch (Exception e) {
//			Log.e("MAIN", "Error setting data source");
//		}
//		byte[] art = meta.getEmbeddedPicture();
//		if (art != null) {
//			Bitmap image = BitmapFactory.decodeByteArray(art, 0, art.length);
//			currImage.setImageBitmap(image);
//		} else {
//			currImage.setImageDrawable(getResources().getDrawable(
//					R.drawable.no_album_image));
//		}
//		currTitle.setText(currSong.getTitle());
//		currArtist.setText(currSong.getArtist());
//
//		meta.release();
//
//	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(receiver);
		receiver = null;
//		if (musicSer.isPlaying() != true) {
//			stopService(playIntent);
//			musicSer = null;
//		}
		super.onDestroy();
	}

}
