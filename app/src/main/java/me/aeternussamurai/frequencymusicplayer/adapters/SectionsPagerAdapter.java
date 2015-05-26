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
    private int screen_size;

    public SectionsPagerAdapter(FragmentManager fm, Context c, int screen_size){
        super(fm);
        context = c;
        this.screen_size = screen_size;
    }

    @Override
    public Fragment getItem(int i) {
        // Get a new instance of the fragment
        ListMenusFragment frag = ListMenusFragment.newInstance(i+1);
        // Create and set an adapter for the inner list view
        // Here is where the cursor adapter will be created based on the position of the ViewPager
        ContentResolver cr = context.getContentResolver();
        Uri target;
        String[] projection;
        String sortOrder;
        int[] views;
        int layout;
        if(i == 1){
            // Artists Data gathering
            target = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Artists._ID, "REPLACE("+MediaStore.Audio.Artists.ARTIST+", '<unknown>', 'Unknown')",MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};
            sortOrder = "REPLACE("+MediaStore.Audio.Artists.ARTIST+", '<unknown>', 'Unknown') COLLATE NOCASE";
            layout = R.layout.cursor_artist_layout;
            views = new int[]{R.id.cursor_artist_layout_ID, R.id.cursor_artist_layout_artist, R.id.cursor_artist_layout_numAlbums};
        }else if(i == 2){
            // Albums Data gathering
            target = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Albums._ID, "REPLACE("+MediaStore.Audio.Albums.ALBUM+", '<unknown>', 'Unknown')", MediaStore.Audio.Albums.ALBUM_ART};
            sortOrder = "REPLACE("+MediaStore.Audio.Albums.ALBUM+", '<unknown>', 'Unknown') COLLATE NOCASE";
            layout = R.layout.cursor_album_layout;
            views = new int[]{R.id.cursor_album_layout_ID, R.id.cursor_album_layout_album, R.id.cursor_album_layout_album_image};
        }else if(i == 3){
            // Playlists Data gathering
            target = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
            sortOrder = MediaStore.Audio.Playlists.NAME + " COLLATE NOCASE";
            layout = R.layout.cursor_playlist_layout;
            views = new int[]{R.id.cursor_playlist_layout_ID, R.id.cursor_playlist_layout_playlist};
        }else{
            // Songs Data gathering, aka i = 0
            target = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Media._ID, "REPLACE("+MediaStore.Audio.Media.ALBUM+", '<unknown>', 'Unknown')", "REPLACE("+MediaStore.Audio.Media.ARTIST+", '<unknown>', 'Unknown')", MediaStore.Audio.Media.TITLE};
            sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE";
            layout = R.layout.cursor_song_layout;
            views = new int[]{R.id.cursor_song_layout_ID, R.id.cursor_song_layout_album, R.id.cursor_song_layout_artist, R.id.cursor_song_layout_title};
        }
        Cursor cursor = cr.query(target,projection,null,null,sortOrder);
        EnhancedSimpleCursorAdapter adapter = new EnhancedSimpleCursorAdapter(context, layout, cursor, projection, views, 2, screen_size);
        frag.setAdapter(adapter);
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
