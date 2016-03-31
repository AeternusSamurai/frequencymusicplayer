package me.aeternussamurai.frequencymusicplayer.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.ref.WeakReference;

import me.aeternussamurai.frequencymusicplayer.R;

/**
 * Created by Chase on 1/30/2016.
 */
public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleViewHolder> {

    private final String DEFAULT_UNKNOWN_VALUE = "Unknown";
    private final int DEFAULT_IMAGE_RESOURCE = R.drawable.no_album_image_v2;

    private int layout;
    private int[] from;
    private int[] to;
    private String[] originalFrom;

    private Bitmap placeHolder;
    private int screen_size;
    private int image_division;
    private Context context;

    public SimpleCursorRecyclerAdapter(int layout, Cursor c, String[] from, int[] to, int screen_size, int image_division, Context context) {
        super(c);
        this.layout = layout;
        this.to = to;
        this.originalFrom = from;
        this.screen_size = screen_size;
        this.image_division = image_division;
        this.context = context;
        findColumns(c, from);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new SimpleViewHolder(v, to);
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
                if (to[i] == R.id.cursor_album_layout_album_image) {
                    text = "" + DEFAULT_IMAGE_RESOURCE;
                }
            }
            if (holder.views[i] instanceof TextView) {
                setViewText((TextView) holder.views[i], text);
            } else if (holder.views[i] instanceof ImageView) {
                holder.views[i].getLayoutParams().height = screen_size / image_division;
                holder.views[i].getLayoutParams().width = screen_size / image_division;
                setViewImage((ImageView) holder.views[i]);
            } else {
                throw new IllegalStateException(holder.views[i].getClass().getName() + " is not a view used by this SimpleCursorRecyclerAdapter");
            }
        }
    }

    private void setViewText(TextView v, String text) {
        v.setText(text);
    }

    private void setViewImage(ImageView v) {
        Cursor tempCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DATA}, MediaStore.Audio.Media.ALBUM_ID + " = ?", new String[]{cursor.getString(from[0])}, null);
        if (tempCursor.moveToFirst()) {
            String path = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            loadBitmapImage(path, v);
            tempCursor.close();
        } else {
            v.setImageResource(DEFAULT_IMAGE_RESOURCE);
        }
    }

    private void loadBitmapImage(String albumPath, ImageView albumImage) {
        if (cancelPotentailWork(albumPath, albumImage)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(albumImage);
            placeHolder = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.no_album_image_v2)).getBitmap();
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), placeHolder, task);
            albumImage.setImageDrawable(asyncDrawable);
            task.execute(albumPath);
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private static boolean cancelPotentailWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = null;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            MediaMetadataRetriever meta = new MediaMetadataRetriever();
            Uri dataUri = Uri.fromFile(new File(data));
            try {
                meta.setDataSource(context, dataUri);
            } catch (IllegalArgumentException iae) {
                //meh
            }
            byte[] art = meta.getEmbeddedPicture();
            meta.release();
            if (art != null) {
                return decodeSampledByteArray(art, screen_size / image_division, screen_size / image_division);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        public Bitmap decodeSampledByteArray(byte[] art, int reqHeight, int reqWidth) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(art, 0, art.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(art, 0, art.length, options);
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                int halfHeight = height / 2;
                int halfWidth = width / 2;

                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }


    }

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
}

class SimpleViewHolder extends RecyclerView.ViewHolder {

    public View[] views;

    public SimpleViewHolder(View itemView, int[] to) {
        super(itemView);
        views = new View[to.length];
        for (int i = 0; i < to.length; i++) {
            views[i] = itemView.findViewById(to[i]);
        }
    }
}