package me.aeternussamurai.frequencymusicplayer.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import me.aeternussamurai.frequencymusicplayer.adapters.SimpleCursorRecyclerAdapter;
import me.aeternussamurai.frequencymusicplayer.adapters.WindowTag;

/**
 * Created by Chase on 5/16/2015.
 */
public class ListMenusFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SimpleCursorRecyclerAdapter adapter;
    private RecyclerView recyclerView;

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
        recyclerView = (RecyclerView)rootView.findViewById(R.id.listing);
        //TextView testText = (TextView) rootView.findViewById(R.id.test_text);
        //testText.setText("Section " + this.getArguments().getInt(ARG_SECTION_NUMBER));
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
        WindowTag tag;
        if(getArguments().getInt(ARG_SECTION_NUMBER)-1 == 1){
            // Artists Data gathering
            target = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Artists._ID, "REPLACE("+MediaStore.Audio.Artists.ARTIST+", '<unknown>', 'Unknown')",MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};
            sortOrder = "REPLACE("+MediaStore.Audio.Artists.ARTIST+", '<unknown>', 'Unknown') COLLATE NOCASE";
            layout = R.layout.cursor_artist_layout;
            views = new int[]{R.id.cursor_artist_layout_ID, R.id.cursor_artist_layout_artist, R.id.cursor_artist_layout_numAlbums};
            tag = WindowTag.ARTIST;
        }else if(getArguments().getInt(ARG_SECTION_NUMBER)-1 == 2){
            // Albums Data gathering
            target = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Albums._ID, "REPLACE("+MediaStore.Audio.Albums.ALBUM+", '<unknown>', 'Unknown')", MediaStore.Audio.Albums.ALBUM_ART};
            sortOrder = "REPLACE("+MediaStore.Audio.Albums.ALBUM+", '<unknown>', 'Unknown') COLLATE NOCASE";
            layout = R.layout.cursor_album_layout;
            views = new int[]{R.id.cursor_album_layout_ID, R.id.cursor_album_layout_album, R.id.cursor_album_layout_album_image};
            tag = WindowTag.ALBUM;
        }else if(getArguments().getInt(ARG_SECTION_NUMBER)-1 == 3){
            // Playlists Data gathering
            target = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
            sortOrder = MediaStore.Audio.Playlists.NAME + " COLLATE NOCASE";
            layout = R.layout.cursor_playlist_layout;
            views = new int[]{R.id.cursor_playlist_layout_ID, R.id.cursor_playlist_layout_playlist};
            tag = WindowTag.PLAYLIST;
        }else{
            // Songs Data gathering, aka i = 0
            target = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Media._ID, "REPLACE("+MediaStore.Audio.Media.ALBUM+", '<unknown>', 'Unknown')", "REPLACE("+MediaStore.Audio.Media.ARTIST+", '<unknown>', 'Unknown')", MediaStore.Audio.Media.TITLE};
            sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE";
            layout = R.layout.cursor_song_layout;
            views = new int[]{R.id.cursor_song_layout_ID, R.id.cursor_song_layout_album, R.id.cursor_song_layout_artist, R.id.cursor_song_layout_title};
            tag = WindowTag.SONG;
        }
        Cursor cursor = cr.query(target,projection,null,null,sortOrder);
        adapter = new SimpleCursorRecyclerAdapter(layout, cursor, projection, views, getScreenSize(), getImageDivision(), getActivity(), tag, Long.valueOf(-1));
        //setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.getCursor().close();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
            // Set the recyclerView to do the grid layout
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
        } else {
            // Set the recyclerView to do the linear layout
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
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
