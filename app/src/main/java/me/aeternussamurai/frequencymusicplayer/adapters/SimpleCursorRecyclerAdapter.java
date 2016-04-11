package me.aeternussamurai.frequencymusicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.aeternussamurai.frequencymusicplayer.R;
import me.aeternussamurai.frequencymusicplayer.SelectionActivity;
import me.aeternussamurai.frequencymusicplayer.glide.MusicCover;
import me.aeternussamurai.frequencymusicplayer.service.MusicService;

/**
 * Created by Chase on 1/30/2016.
 */
public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleCursorRecyclerAdapter.SimpleViewHolder> {

    private final String DEFAULT_UNKNOWN_VALUE = "Unknown";
    private final int DEFAULT_IMAGE_RESOURCE = R.drawable.no_album_image_v2;

    private MusicService service = MusicService.getInstance();

    private int layout;
    private int[] from;
    private int[] to;
    private String[] originalFrom;

    private Bitmap placeHolder;
    private int screen_size;
    private int image_division;
    private Context context;
    private WindowTag tag;

    private Long artistID; // used by the adapter to find the correct songs with the ARTIST_ALBUM tag

    public SimpleCursorRecyclerAdapter(int layout, Cursor c, String[] from, int[] to, int screen_size, int image_division, Context context, WindowTag tag, Long artistID) {
        super(c);
        this.layout = layout;
        this.to = to;
        this.originalFrom = from;
        this.screen_size = screen_size;
        this.image_division = image_division;
        this.context = context;
        this.tag = tag;
        this.artistID = artistID;
        findColumns(c, from);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new SimpleViewHolder(v, to, tag);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, Cursor cursor) {
        final int count = to.length;
        final int[] from = this.from;

        for (int i = 0; i < count; i++) {
            // fill the views in the view holder
            String text = cursor.getString(from[i]);
            if (text == null) {
                text = "";
                if (to[i] == R.id.cursor_album_layout_album_image
                        || to[i] == R.id.selection_artist_album_image
                        || to[i] == R.id.selection_playlist_album_image) {
                    text = "" + DEFAULT_IMAGE_RESOURCE;
                }
            }
            if (holder.views[i] instanceof TextView) {
                if (to[i] == R.id.selection_album_song_duration
                        || to[i] == R.id.selection_playlist_song_duration) {
                    text = getReadableTime(cursor.getLong(from[i]));
                }
                if (to[i] == R.id.selection_album_track_no) {
                    text = getReadableTrackNo(cursor.getInt(from[i]));
                }
                if (to[i] == R.id.cursor_album_layout_album
                        || to[i] == R.id.cursor_artist_layout_artist
                        || to[i] == R.id.cursor_playlist_layout_playlist
                        || to[i] == R.id.cursor_song_layout_title
                        || to[i] == R.id.selection_artist_album_name) {
                    holder.itemView.setTag(text);
                }
                setViewText((TextView) holder.views[i], text);
            } else if (holder.views[i] instanceof ImageView) {
                if(screen_size != 0 && image_division != 0) {
                    holder.views[i].getLayoutParams().height = screen_size / image_division;
                    holder.views[i].getLayoutParams().width = screen_size / image_division;
                }
                setViewImage((ImageView) holder.views[i]);
            } else {
                throw new IllegalStateException(holder.views[i].getClass().getName() + " is not a view used by this SimpleCursorRecyclerAdapter");
            }
        }
    }

    private String getReadableTrackNo(Integer track) {
        String temp = "";
        if (track > 1000) {
            track %= 1000;
        }
        temp += track;
        return temp;
    }

    private String getReadableTime(Long dur) {
        long durSec = dur / 1000;
        long durMin = durSec / 60;
        long actDurSec = durSec % 60;
        String temp = durMin + ":" + actDurSec;
        return temp;
    }

    private void setViewText(TextView v, String text) {
        v.setText(text);
    }

    private void setViewImage(ImageView v) {
        Cursor tempCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DATA}, ((tag == WindowTag.PLAYLIST_SONG) ? MediaStore.Audio.Media._ID : MediaStore.Audio.Media.ALBUM_ID) + " = ?", new String[]{cursor.getString(from[0])}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER + " LIMIT 1");
        if (tempCursor.moveToFirst()) {
            String path = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Glide.with(context)
                    .load(new MusicCover(path))
                    .placeholder(R.drawable.no_album_image_v2)
                    .error(R.drawable.no_album_image_v2)
                    .into(v);
//            loadBitmapImage(path, v);
            tempCursor.close();
        } else {
            v.setImageResource(DEFAULT_IMAGE_RESOURCE);
        }
    }

//    private void loadBitmapImage(String albumPath, ImageView albumImage) {
//        if (cancelPotentailWork(albumPath, albumImage)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(albumImage);
//            placeHolder = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.no_album_image_v2)).getBitmap();
//            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), placeHolder, task);
//            albumImage.setImageDrawable(asyncDrawable);
//            task.execute(albumPath);
//        }
//    }
//
//    private static class AsyncDrawable extends BitmapDrawable {
//        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
//
//        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
//            super(res, bitmap);
//            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
//        }
//
//        public BitmapWorkerTask getBitmapWorkerTask() {
//            return bitmapWorkerTaskReference.get();
//        }
//    }
//
//    private static boolean cancelPotentailWork(String data, ImageView imageView) {
//        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//        if (bitmapWorkerTask != null) {
//            final String bitmapData = bitmapWorkerTask.data;
//            if (bitmapData == null || !bitmapData.equals(data)) {
//                bitmapWorkerTask.cancel(true);
//            } else {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
//        if (imageView != null) {
//            final Drawable drawable = imageView.getDrawable();
//            if (drawable instanceof AsyncDrawable) {
//                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
//                return asyncDrawable.getBitmapWorkerTask();
//            }
//        }
//        return null;
//    }
//
//    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
//        private final WeakReference<ImageView> imageViewReference;
//        private String data = null;
//
//        public BitmapWorkerTask(ImageView imageView) {
//            imageViewReference = new WeakReference<ImageView>(imageView);
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            data = params[0];
//            MediaMetadataRetriever meta = new MediaMetadataRetriever();
//            Uri dataUri = Uri.fromFile(new File(data));
//            try {
//                meta.setDataSource(context, dataUri);
//            } catch (IllegalArgumentException iae) {
//                //meh
//            }
//            byte[] art = meta.getEmbeddedPicture();
//            meta.release();
//            if (art != null) {
//                return decodeSampledByteArray(art, screen_size / image_division, screen_size / image_division);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (imageViewReference != null && bitmap != null) {
//                final ImageView imageView = imageViewReference.get();
//                if (imageView != null) {
//                    imageView.setImageBitmap(bitmap);
//                }
//            }
//        }
//
//        public Bitmap decodeSampledByteArray(byte[] art, int reqHeight, int reqWidth) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeByteArray(art, 0, art.length, options);
//            options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeByteArray(art, 0, art.length, options);
//        }
//
//        private int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
//            int height = options.outHeight;
//            int width = options.outWidth;
//            int inSampleSize = 1;
//
//            if (height > reqHeight || width > reqWidth) {
//                int halfHeight = height / 2;
//                int halfWidth = width / 2;
//
//                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
//                    inSampleSize *= 2;
//                }
//            }
//            return inSampleSize;
//        }
//
//
//    }

    private void findColumns(Cursor c, String[] from) {
        if (c != null) {
            int i;
            int count = from.length;
            if (this.from == null || this.from.length != count) {
                this.from = new int[count];
            }
            for (i = 0; i < count; i++) {
                this.from[i] = c.getColumnIndexOrThrow(from[i]);
            }
        } else {
            this.from = null;
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        findColumns(c, originalFrom);
        return super.swapCursor(c);
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View[] views;
        public WindowTag tag;

        public SimpleViewHolder(View itemView, int[] to, WindowTag tag) {
            super(itemView);
            views = new View[to.length];
            for (int i = 0; i < to.length; i++) {
                views[i] = itemView.findViewById(to[i]);
            }
            this.tag = tag;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (tag == WindowTag.SONG || tag == WindowTag.ALBUM_SONG || tag == WindowTag.PLAYLIST_SONG) {
                // setUp the music list from the music service
                // start the NowPlaying activity
                Log.d("CURSOR_ADAPTER_VH", "OnClick : Song selection");
            } else {
                Log.d("CURSOR_ADAPTER_VH", "OnClick : Parent Selection");
                Intent selection = new Intent(context, SelectionActivity.class);
                selection.putExtra("WINDOW_TAG", tag);
                selection.putExtra("SELECTION_ID", Long.parseLong(((TextView) views[0]).getText().toString()));
                selection.putExtra("SELECTION_TITLE", (String) itemView.getTag());
                if(artistID != -1){
                    selection.putExtra("ARTIST_ID", artistID);
                }
                context.startActivity(selection);
            }
        }
    }
}

