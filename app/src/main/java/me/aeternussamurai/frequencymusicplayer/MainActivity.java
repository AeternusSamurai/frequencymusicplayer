package me.aeternussamurai.frequencymusicplayer;

import java.util.Locale;


import android.app.FragmentManager;
import android.graphics.Point;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import me.aeternussamurai.frequencymusicplayer.adapters.SectionsPagerAdapter;
import me.aeternussamurai.frequencymusicplayer.fragments.CurrentSongExtraFragment;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    /**
     * The {@link FragmentStatePagerAdapter} that will provide
     * fragments for each of the sections.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    TabLayout tabLayout;
    private int image_division;
    private boolean extrasVisible;

    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screen_size = getDeviceScreenSize();
        setUpCurrentSongContainer(screen_size);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the four
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        detector = new GestureDetector(this,this);
        Toolbar curSongLayout = (Toolbar)findViewById(R.id.main_screen_curent_song_container);
        curSongLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean result = detector.onTouchEvent(event);
                return result;
            }
        });

    }

    /**
     * Get the size of the screen of the device
     * @return the size of the screen
     */
    private int getDeviceScreenSize() {
        Display ds = getWindowManager().getDefaultDisplay();
        Point pt = new Point();
        ds.getSize(pt);
        if(ds.getRotation() == Surface.ROTATION_90 || ds.getRotation() == Surface.ROTATION_270){
            image_division = 14;
        }else{
            image_division = 7;
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
    public void onStart(){
        super.onStart();
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
        if(extrasVisible){

        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onShowPress: " + e.toString());
    }

    // Use this method to handle the change to the NowPlaying Activity
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
        // if the change in y is greater than the change in x
        // if the first event's y is less than the second event's y
        // otherwise return false
        boolean result;
        float deltaX = Math.abs(e1.getX() - e2.getX());
        float deltaY = Math.abs(e1.getY() - e2.getY());
        if(deltaY > deltaX){
            if(e1.getY() > e2.getY()){
                // from the bottom to the top
                result = true;
                extrasVisible = true;
                findViewById(R.id.extras_container).setVisibility(View.VISIBLE);
                //do the slide view thing
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down)
                        .add(R.id.extras_container, Fragment.instantiate(getApplicationContext(), CurrentSongExtraFragment.class.getName()), "extra")
                        .addToBackStack(null).commit();
            }else{
                // from the top to the bottom
                getFragmentManager().popBackStack();
                result = false;
            }
        }else{
            // Not a vertical swipe.
            result = false;
        }
        return result;
    }
}
