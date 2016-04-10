package me.aeternussamurai.frequencymusicplayer;

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
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import me.aeternussamurai.frequencymusicplayer.adapters.HeaderRecyclerAdapter;
import me.aeternussamurai.frequencymusicplayer.adapters.SimpleCursorRecyclerAdapter;
import me.aeternussamurai.frequencymusicplayer.adapters.WindowTag;
import me.aeternussamurai.frequencymusicplayer.service.MusicService;

public class SelectionActivity extends AppCompatActivity{

    private WindowTag tag;
    private Long selectionID;
    private String selectionTitle;
    private SimpleCursorRecyclerAdapter adapter;
    private HeaderRecyclerAdapter headerAdapter;

    private MusicService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        service = MusicService.getInstance();

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
            sortOrder = null;
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
            sortOrder = MediaStore.Audio.Albums.ALBUM_KEY;
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
            sortOrder = null;
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

}
