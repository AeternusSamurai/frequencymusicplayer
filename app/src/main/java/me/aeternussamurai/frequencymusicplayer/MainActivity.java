package me.aeternussamurai.frequencymusicplayer;

import java.util.Locale;


import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ToggleButton;

import me.aeternussamurai.frequencymusicplayer.adapters.SectionsPagerAdapter;


public class MainActivity extends FragmentActivity {

    /**
     * The {@link FragmentStatePagerAdapter} that will provide
     * fragments for each of the sections.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screen_size = getDeviceScreenSize();
        setUpCurrentSongContainer(screen_size);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), screen_size);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    private int getDeviceScreenSize() {
        Display ds = getWindowManager().getDefaultDisplay();
        Point pt = new Point();
        ds.getSize(pt);
        return pt.x;
    }

    private void setUpCurrentSongContainer(int screen_size){
        // set the sizes of the imageview and the toggle button so that they fit well within the screen
        ImageView album_image = (ImageView) findViewById(R.id.ms_csc_song_image);
        album_image.getLayoutParams().height = screen_size/6;
        album_image.getLayoutParams().width = screen_size/6;

        ToggleButton play_pause_button = (ToggleButton) findViewById(R.id.ms_csc_play_pause_button);
        play_pause_button.getLayoutParams().height = screen_size/6;
        play_pause_button.getLayoutParams().width = screen_size/6;
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
}
