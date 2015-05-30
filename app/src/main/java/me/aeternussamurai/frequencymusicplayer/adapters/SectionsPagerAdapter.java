package me.aeternussamurai.frequencymusicplayer.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import java.util.Locale;

import me.aeternussamurai.frequencymusicplayer.R;
import me.aeternussamurai.frequencymusicplayer.fragments.ListMenusFragment;

/**
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter{

    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context c){
        super(fm);
        context = c;
    }

    @Override
    public Fragment getItem(int i) {
        // Get a new instance of the fragment
        ListMenusFragment frag = ListMenusFragment.newInstance(i+1);
        return frag;

    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position){
            case 0:
                return context.getString(R.string.title_song_section).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_artist_section).toUpperCase(l);
            case 2:
                return context.getString(R.string.title_album_section).toUpperCase(l);
            case 3:
                return context.getString(R.string.title_playlist_section).toUpperCase(l);
        }
        return null;
    }
}
