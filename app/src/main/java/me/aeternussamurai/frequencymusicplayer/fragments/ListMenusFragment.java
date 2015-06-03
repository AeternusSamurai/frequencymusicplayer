package me.aeternussamurai.frequencymusicplayer.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import me.aeternussamurai.frequencymusicplayer.R;
import me.aeternussamurai.frequencymusicplayer.adapters.EnhancedSimpleCursorAdapter;

/**
 * Created by Chase on 5/16/2015.
 */
public class ListMenusFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private EnhancedSimpleCursorAdapter adapter;
    private GridView grid;
    private ListView list;

    public static ListMenusFragment newInstance(int sectionNumber){
        ListMenusFragment fragment = new ListMenusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ListMenusFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container,false);
        list = (ListView)rootView.findViewById(R.id.listing);
        grid = (GridView) rootView.findViewById(R.id.album_grid);
        TextView testText = (TextView) rootView.findViewById(R.id.test_text);
        testText.setText("Section " + this.getArguments().getInt(ARG_SECTION_NUMBER));
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ContentResolver cr = getActivity().getContentResolver();
        Uri target;
        String[] projection;
        String sortOrder;
        int[] views;
        int layout;
        if(getArguments().getInt(ARG_SECTION_NUMBER)-1 == 1){
            // Artists Data gathering
            target = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Artists._ID, "REPLACE("+MediaStore.Audio.Artists.ARTIST+", '<unknown>', 'Unknown')",MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};
            sortOrder = "REPLACE("+MediaStore.Audio.Artists.ARTIST+", '<unknown>', 'Unknown') COLLATE NOCASE";
            layout = R.layout.cursor_artist_layout;
            views = new int[]{R.id.cursor_artist_layout_ID, R.id.cursor_artist_layout_artist, R.id.cursor_artist_layout_numAlbums};
        }else if(getArguments().getInt(ARG_SECTION_NUMBER)-1 == 2){
            // Albums Data gathering
            target = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Albums._ID, "REPLACE("+MediaStore.Audio.Albums.ALBUM+", '<unknown>', 'Unknown')", MediaStore.Audio.Albums.ALBUM_ART};
            sortOrder = "REPLACE("+MediaStore.Audio.Albums.ALBUM+", '<unknown>', 'Unknown') COLLATE NOCASE";
            layout = R.layout.cursor_album_layout;
            views = new int[]{R.id.cursor_album_layout_ID, R.id.cursor_album_layout_album, R.id.cursor_album_layout_album_image};
        }else if(getArguments().getInt(ARG_SECTION_NUMBER)-1 == 3){
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
        adapter = new EnhancedSimpleCursorAdapter(getActivity(), layout, cursor, projection, views, 2, getScreenSize(), getImageDivision());
        //setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
            list.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
            grid.setAdapter(adapter);
        } else {
            grid.setVisibility(View.GONE);
            list.setAdapter(adapter);
        }
    }

    private int getScreenSize(){
        Display ds = getActivity().getWindowManager().getDefaultDisplay();
        Point pt = new Point();
        ds.getSize(pt);
        return pt.x;
    }

    private int getImageDivision(){
        Display ds = getActivity().getWindowManager().getDefaultDisplay();
        //Point pt = new Point();
        if(ds.getRotation() == Surface.ROTATION_90 || ds.getRotation() == Surface.ROTATION_270){
            return 4;
        }else{
            return 3;
        }
    }
}
