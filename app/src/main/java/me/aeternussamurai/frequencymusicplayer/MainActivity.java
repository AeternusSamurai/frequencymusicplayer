package me.aeternussamurai.frequencymusicplayer;

import java.util.Locale;


import android.gesture.Gesture;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import me.aeternussamurai.frequencymusicplayer.adapters.SectionsPagerAdapter;


public class MainActivity extends FragmentActivity implements GestureDetector.OnGestureListener {

    /**
     * The {@link FragmentStatePagerAdapter} that will provide
     * fragments for each of the sections.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private int image_division;
    private int album_image_division;

    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screen_size = getDeviceScreenSize();
        setUpCurrentSongContainer(screen_size);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        RelativeLayout curSongLayout = (RelativeLayout)findViewById(R.id.main_screen_curent_song_container);
        detector = new GestureDetector(this,this);

    }

    private int getDeviceScreenSize() {
        Display ds = getWindowManager().getDefaultDisplay();
        Point pt = new Point();
        ds.getSize(pt);
        if(ds.getRotation() == Surface.ROTATION_90 || ds.getRotation() == Surface.ROTATION_270){
            image_division = 14;
            album_image_division = 4;
        }else{
            image_division = 7;
            album_image_division = 3;
        }
        return pt.x;
    }

    private void setUpCurrentSongContainer(int screen_size){
        // set the sizes of the imageview and the toggle button so that they fit well within the screen
        ImageView album_image = (ImageView) findViewById(R.id.ms_csc_song_image);
        album_image.getLayoutParams().height = screen_size/image_division;
        album_image.getLayoutParams().width = screen_size/image_division;

        ToggleButton play_pause_button = (ToggleButton) findViewById(R.id.ms_csc_play_pause_button);
        play_pause_button.getLayoutParams().height = screen_size/image_division;
        play_pause_button.getLayoutParams().width = screen_size/image_division;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // do nothing for now on onDown, onShowPress, onSingleTapUp, onScroll, onLongPress
    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onDown: " + e.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onShowPress: " + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onSingleTapUp: " + e.toString());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("FMP_GESTURE_TEST", "onScroll: " + e1.toString() + e2.toString());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onLongPress" + e.toString());
    }

    // Determine which direction the gesture is going
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("FMP_GESTURE_TEST", "onFling: " + e1.toString() + e2.toString());
        return false;
    }
}
