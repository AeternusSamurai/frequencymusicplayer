package me.aeternussamurai.frequencymusicplayer;

import android.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import me.aeternussamurai.frequencymusicplayer.adapters.HeaderRecyclerAdapter;
import me.aeternussamurai.frequencymusicplayer.adapters.SimpleCursorRecyclerAdapter;
import me.aeternussamurai.frequencymusicplayer.adapters.WindowTag;
import me.aeternussamurai.frequencymusicplayer.fragments.CurrentSongExtraFragment;
import me.aeternussamurai.frequencymusicplayer.service.MusicService;

public class SelectionActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private WindowTag tag;
    private Long selectionID;
    private String selectionTitle;
    private SimpleCursorRecyclerAdapter adapter;
    private HeaderRecyclerAdapter headerAdapter;

    private boolean extrasVisible;

    private MusicService service;

    private int image_division;

    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        service = MusicService.getInstance();
        int screen_size = getDeviceScreenSize();
        setUpCurrentSongContainer(screen_size);

        tag = (WindowTag) getIntent().getSerializableExtra("WINDOW_TAG");
        selectionID = getIntent().getLongExtra("SELECTION_ID", 0);
        selectionTitle = getIntent().getStringExtra("SELECTION_TITLE");

        getSupportActionBar().setTitle(selectionTitle);

        ContentResolver cr = getContentResolver();
        Uri target;
        String[] projection;
        String sortOrder;
        int[] views;
        int layout;
        String whereSelection;
        String[] whereArgs;
        WindowTag nextTag;
        String headerInfo;
        String headerImagePath;

        Cursor cursor;
        Long artistID = getIntent().getLongExtra("ARTIST_ID", -1);


        if (tag == WindowTag.ALBUM) {
            // Albums
            // Song id, track no., title, artist, duration
            target = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.TITLE, "REPLACE(" + MediaStore.Audio.Media.ARTIST + ", '<unknown>', 'Unknown')", MediaStore.Audio.Media.DURATION};
            sortOrder = MediaStore.Audio.Media.TRACK;
            layout = R.layout.selection_album_layout;
            views = new int[]{R.id.selection_album_song_id, R.id.selection_album_track_no, R.id.selection_album_song_name, R.id.selection_album_song_artist, R.id.selection_album_song_duration};
            whereSelection = MediaStore.Audio.Media.ALBUM_ID + " = ?";
            whereArgs = new String[]{selectionID+""};
            nextTag = WindowTag.ALBUM_SONG;
            cursor = cr.query(target, projection, whereSelection, whereArgs, sortOrder);
            headerInfo = retrieveSongCount(MediaStore.Audio.Media.ALBUM_ID, selectionID) + " " + retrieveTotalTime(MediaStore.Audio.Media.ALBUM_ID, selectionID);
            headerImagePath = retrieveImagePath(MediaStore.Audio.Media.ALBUM_ID, selectionID);
        } else if (tag == WindowTag.ARTIST) {
            // Artists
            // Album id, name, year, image
            target = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.LAST_YEAR};
            sortOrder = MediaStore.Audio.Albums.ALBUM;
            layout = R.layout.selection_artist_layout;
            views = new int[]{R.id.selection_artist_album_id ,R.id.selection_artist_album_name, R.id.selection_artist_album_image, R.id.selection_artist_album_year};
            whereArgs = getAlbumIDs(selectionID);
            whereSelection = getAlbumSelection(whereArgs);
            nextTag = WindowTag.ARTIST_ALBUM;
            artistID = selectionID;
            cursor = cr.query(target, projection, whereSelection, whereArgs, sortOrder);
            headerInfo = retrieveSongCount(MediaStore.Audio.Media.ARTIST_ID, selectionID) + " " + retrieveTotalTime(MediaStore.Audio.Media.ARTIST_ID, selectionID);
            headerImagePath = retrieveImagePath(MediaStore.Audio.Media.ARTIST_ID, selectionID);
        } else if(tag == WindowTag.ARTIST_ALBUM){
            // Albums
            // Song id, track no., title, artist, duration
            target = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.TITLE, "REPLACE(" + MediaStore.Audio.Media.ARTIST + ", '<unknown>', 'Unknown')", MediaStore.Audio.Media.DURATION};
            sortOrder = MediaStore.Audio.Media.TRACK;
            layout = R.layout.selection_album_layout;
            views = new int[]{R.id.selection_album_song_id, R.id.selection_album_track_no, R.id.selection_album_song_name, R.id.selection_album_song_artist, R.id.selection_album_song_duration};
            whereSelection = MediaStore.Audio.Media.ALBUM_ID + " = ? AND " + MediaStore.Audio.Media.ARTIST_ID + " = ?";
            whereArgs = new String[]{selectionID+"", artistID+""};
            nextTag = WindowTag.ALBUM_SONG;
            cursor = cr.query(target, projection, whereSelection, whereArgs, sortOrder);
            headerInfo = retrieveSongCount(whereSelection, whereArgs) + " " + retrieveTotalTime(whereSelection, whereArgs);
            headerImagePath = retrieveImagePath(whereSelection, whereArgs);
        } else {
            // Playlists
            // Song id, title, artist, image, duration
            target = MediaStore.Audio.Playlists.Members.getContentUri("external",selectionID);
            projection = new String[]{MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.ARTIST, MediaStore.Audio.Playlists.Members.ALBUM, MediaStore.Audio.Playlists.Members.DURATION};
            sortOrder = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            layout = R.layout.selection_playlist_layout;
            views = new int[]{R.id.selection_playlist_song_id, R.id.selection_playlist_song_id_2, R.id.selection_playlist_song_name, R.id.selection_playlist_song_artist, R.id.selection_playlist_album_image, R.id.selection_playlist_song_duration};
            whereSelection = null;
            whereArgs = null;
            nextTag = WindowTag.PLAYLIST_SONG;
            cursor = cr.query(target, projection, whereSelection, whereArgs, sortOrder);
            headerInfo = retrieveSongCount(target) + " " + retrieveTotalTime(target);
            headerImagePath = "";
        }


        adapter = new SimpleCursorRecyclerAdapter(layout, cursor, projection, views, 0, 0, this, nextTag, artistID);
        headerAdapter = new HeaderRecyclerAdapter(this, adapter, headerInfo, headerImagePath, tag);



        RecyclerView selectionList = (RecyclerView) findViewById(R.id.selection_listing);
        selectionList.setLayoutManager(new LinearLayoutManager(this));
        selectionList.setAdapter(headerAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detector = new GestureDetector(this, this);
        Toolbar curSongLayout = (Toolbar) findViewById(R.id.main_screen_curent_song_container);
        curSongLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean result = detector.onTouchEvent(event);
                return result;
            }
        });
    }

    private String retrieveImagePath(String idCol, Long selectionID) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DATA}, idCol + " = ?", new String[]{selectionID+""}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER + " LIMIT 1");
        String path = "";
        if(cursor.moveToFirst()){
            int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            path = cursor.getString(dataCol);
        }
        cursor.close();
        return path;
    }

    private String retrieveImagePath(String idCols, String[] args) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DATA}, idCols, args, MediaStore.Audio.Media._ID + " LIMIT 1");
        String path = "";
        if(cursor.moveToFirst()){
            int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            path = cursor.getString(dataCol);
        }
        cursor.close();
        return path;
    }

    private String retrieveTotalTime(String idCol, Long selectionID) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DURATION}, idCol + " = ?", new String[]{selectionID.toString()}, null);
        String totalTime = "";
        if(cursor.moveToFirst()){
            long time = 0;
            int durCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do{
                time += cursor.getLong(durCol);
            }while(cursor.moveToNext());
            totalTime = convertToReadableTime(time);
        }
        totalTime = "(" + totalTime + ")";
        cursor.close();
        return totalTime;
    }

    private String retrieveTotalTime(String idCols, String[] args) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DURATION}, idCols, args, null);
        String totalTime = "";
        if(cursor.moveToFirst()){
            long time = 0;
            int durCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do{
                time += cursor.getLong(durCol);
            }while(cursor.moveToNext());
            totalTime = convertToReadableTime(time);
        }
        totalTime = "(" + totalTime + ")";
        cursor.close();
        return totalTime;
    }

    private String retrieveTotalTime(Uri target) {
        Cursor cursor = getContentResolver().query(target, new String[]{MediaStore.Audio.Playlists.Members.DURATION}, null, null, null);
        String totalTime = "";
        if(cursor.moveToFirst()){
            long time = 0;
            int durCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do{
                time += cursor.getLong(durCol);
            }while(cursor.moveToNext());
            totalTime = convertToReadableTime(time);
        }
        totalTime = "(" + totalTime + ")";
        cursor.close();
        return totalTime;
    }

    private String convertToReadableTime(long time) {
        int hours = (int) (((time / 1000) / 60) / 60);
        int minute = (int) (((time / 1000) / 60) % 60);
        String temp = hours + "h " + minute + "m";
        return temp;
    }

    private String retrieveSongCount(String idCol, Long selectionID) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID}, idCol + " = ?", new String[]{selectionID.toString()}, null);
        String count = cursor.getCount() + " Songs";
        cursor.close();
        return count;
    }

    private String retrieveSongCount(String idCols, String[] args) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID}, idCols, args, null);
        String count = cursor.getCount() + " Songs";
        cursor.close();
        return count;
    }

    private String retrieveSongCount(Uri target) {
        Cursor cursor = getContentResolver().query(target, new String[]{MediaStore.Audio.Playlists.Members._ID}, null, null, null);
        String count = cursor.getCount() + " Songs";
        cursor.close();
        return count;
    }

    private String[] getAlbumIDs(Long artistID) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"DISTINCT " +MediaStore.Audio.Media.ALBUM_ID}, MediaStore.Audio.Media.ARTIST_ID + " = ?", new String[]{artistID.toString()}, null);
        String[] albumIDs = new String[cursor.getCount()];
        if(cursor.moveToFirst()){
            int albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int idx = 0;
            do{
                long albumID = cursor.getLong(albumIdCol);
                albumIDs[idx] = albumID+"";
                idx++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        return albumIDs;
    }

    private String getAlbumSelection(String[] albumIDs) {
        String retVal = "";
        for(int i = 0; i < albumIDs.length; i++){
            retVal += (i == albumIDs.length-1) ? MediaStore.Audio.Albums._ID + " = ?" : MediaStore.Audio.Albums._ID + " = ? OR ";
        }
        return retVal;
    }

    /**
     * Get the size of the screen of the device
     *
     * @return the size of the screen
     */
    private int getDeviceScreenSize() {
        Display ds = getWindowManager().getDefaultDisplay();
        Point pt = new Point();
        ds.getSize(pt);
        if (ds.getRotation() == Surface.ROTATION_90 || ds.getRotation() == Surface.ROTATION_270) {
            image_division = 14;
        } else {
            image_division = 7;
        }
        return pt.x;
    }

    private void setUpCurrentSongContainer(int screen_size) {
        // set the sizes of the imageview and the toggle button so that they fit well within the screen
        ImageView album_image = (ImageView) findViewById(R.id.ms_csc_song_image);
        album_image.getLayoutParams().height = screen_size / image_division;
        album_image.getLayoutParams().width = screen_size / image_division;

        ToggleButton play_pause_button = (ToggleButton) findViewById(R.id.ms_csc_play_pause_button);
        play_pause_button.getLayoutParams().height = screen_size / image_division;
        play_pause_button.getLayoutParams().width = screen_size / image_division;
    }

    // do nothing for now on onDown, onShowPress, onSingleTapUp, onScroll, onLongPress
    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onDown: " + e.toString());
        if (extrasVisible) {

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
        if (deltaY > deltaX) {
            if (e1.getY() > e2.getY()) {
                // from the bottom to the top
                result = true;
                extrasVisible = true;
                findViewById(R.id.extras_container).setVisibility(View.VISIBLE);
                //do the slide view thing
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down)
                        .add(R.id.extras_container, Fragment.instantiate(getApplicationContext(), CurrentSongExtraFragment.class.getName()), "extra")
                        .addToBackStack(null).commit();
            } else {
                // from the top to the bottom
                getFragmentManager().popBackStack();
                result = false;
            }
        } else {
            // Not a vertical swipe.
            result = false;
        }
        return result;
    }
}
