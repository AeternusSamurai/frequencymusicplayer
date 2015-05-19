package me.aeternussamurai.frequencymusicplayer.managers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;

import me.aeternussamurai.frequencymusicplayer.Playlist;
import me.aeternussamurai.frequencymusicplayer.Song;

/**
 * Created by Chase on 2/26/2015.
 */
public class MusicManager {

    private SongManager songManager;
    private ArtistManager artistManager;
    private AlbumManager albumManager;
    private PlaylistManager playlistManager;
    private ContentResolver contentResolver;

    public MusicManager(ContentResolver cr) {
        songManager = new SongManager();
        artistManager = new ArtistManager();
        albumManager = new AlbumManager();
        playlistManager = new PlaylistManager();
        contentResolver = cr;
    }

    /**
     * Get a list of songs from one of the built in managers based on a given key and optional tag.
     *
     * @param key The key from which to pull the list of songs from.
     * @param tag The flag for which manager to get the song lists from. A value of 0 pulls the total list of songs from the Song Manager.
     * @return
     */
    public ArrayList<Song> getSongList(String key, int tag) {
        if (tag == 1)
            return artistManager.getItemByTag(key);
        if (tag == 2)
            return albumManager.getItemByTag(key);
        if (tag == 3)
            return playlistManager.getItemByTag(key).getList();
        return songManager.getSongs();
    }

    private class DataRetriverTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getPlaylists();
            createSongList();
            return null;
        }
    }

    /**
     * Get the information about each song using the system providers.
     * Any related managers must not be null.
     */
    public void createSongList() {
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null,
                null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            final int rowCount = musicCursor.getCount();
            int titleCol = musicCursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idCol = musicCursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistCol = musicCursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int albumCol = musicCursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
            int dataCol = musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA);
            int lengthCol = musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION);
            int trackCol = musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.TRACK);
            int composerCol = musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.COMPOSER);

            do {
                long songID = musicCursor.getLong(idCol);
                String songTitle = musicCursor.getString(titleCol);
                String songArtist = musicCursor.getString(artistCol);
                if (songArtist.equals("<unknown>")) {
                    songArtist = "Unknown";
                }
                String songAlbum = musicCursor.getString(albumCol);
                if (songAlbum.equals("<unknown>")) {
                    songAlbum = "Unknown";
                }
                String songPath = musicCursor.getString(dataCol);
                long songDuration = musicCursor.getLong(lengthCol);
                int track = musicCursor.getInt(trackCol);
                String composer = musicCursor.getString(composerCol);
                if (composer.equals("<unknown>")) {
                    composer = "Unknown";
                }

                String genre = "";
                Uri genreUri = android.provider.MediaStore.Audio.Genres
                        .getContentUriForAudioId("external", (int) songID);
                Cursor genreCursor = contentResolver.query(genreUri,
                        new String[] { MediaStore.Audio.Genres.NAME }, null,
                        null, null);
                if (genreCursor != null && genreCursor.moveToFirst()) {
                    int genreCol = genreCursor
                            .getColumnIndex(MediaStore.Audio.Genres.NAME);
                    do {
                        genre += genreCursor.getString(genreCol);
                    } while (genreCursor.moveToNext());
                }
                genreCursor.close();

                if (genre.equals("")) {
                    genre = "Unknowm";
                }

                Song temp = new Song(songID, songTitle, songArtist, songAlbum, songPath, songDuration, track, composer, genre);
                //Add song to the managers
                songManager.add(temp);
                artistManager.add(temp);
                albumManager.add(temp);
                playlistManager.add(temp);


            } while (musicCursor.moveToNext());

            musicCursor.close();

        }

    }

    /**
     * Get the information about each playlist on the device using system providers.
     * The playlist manager must not be null.
     */
    public void getPlaylists(){
        String[] projection = new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME, MediaStore.Audio.Playlists.DATA};
        Cursor playlistCursor = null;
        //get internal playllists if any
        // It seems that a table for any internally stored playlists doesn't exist.
        // Maybe able to resolve this by saving to (thus creating) an internally storage table
        Uri interanlPlaylistUri = MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI;
        try{
            playlistCursor = contentResolver.query(interanlPlaylistUri, projection, null, null, null);
        }catch(Exception e){

        }
        if(playlistCursor != null && playlistCursor.moveToFirst()){
            getPlaylistInfo(contentResolver, playlistCursor, "internal");
            playlistCursor.close();
        }

        // get external playlists
        Uri externalPlaylistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        playlistCursor = contentResolver.query(externalPlaylistUri, projection, null, null, null);
        if(playlistCursor != null && playlistCursor.moveToFirst()){
            getPlaylistInfo(contentResolver, playlistCursor, "external");
            playlistCursor.close();
        }

    }

    /**
     * Helper method to get the information about each playlist on the device using the system providers minus the actual songs themselves
     * @param playlistResolver The system resolver that queried for the playlist information.
     * @param playlistCursor The cursor that contains that information to the playlist table in the provider.
     * @param storage The string that defines where the information comes from internal storage or external storage.
     */
    //TODO Adjust the playlist manager to reflect dymanic adding of SONGS (not playLists) to the manager.
    private void getPlaylistInfo(ContentResolver playlistResolver, Cursor playlistCursor, String storage){
        int idCol = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
        int nameCol = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
        int dataCol = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.DATA);
        do{
            // get the data for the playlists
            long playlistID = playlistCursor.getLong(idCol);
            String playListName = playlistCursor.getString(nameCol);
            String playListPath = playlistCursor.getString(dataCol);
            Playlist playlist = new Playlist(playlistID, playListName, playListPath);

            // find all of the songs by ID that go in the playlist
            Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(storage, playlistID);
            Cursor playlistMembers = playlistResolver.query(membersUri, new String[]{MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members.PLAY_ORDER}, null, null, null);
            if(playlistMembers != null && playlistMembers.moveToFirst()){
                int audioIdCol = playlistMembers.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
                do{
                    long songId = playlistMembers.getLong(audioIdCol);
                    playlist.add(songId);
                }while(playlistMembers.moveToNext());
            }
            playlistMembers.close();
            playlistManager.add(playlist);


        }while (playlistCursor.moveToNext());
        playlistCursor.close();
    }

}