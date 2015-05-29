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
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;

import me.aeternussamurai.frequencymusicplayer.R;

/**
 * Created by Chase on 5/17/2015.
 */
public class EnhancedSimpleCursorAdapter extends SimpleCursorAdapter {

    private final String DEFAULT_UNKNOWN_VALUE = "Unknown";
    private final int DEFAUULT_IMAGE_RESOURCE = R.drawable.no_album_image_v2;

    private ViewBinder mViewBinder;

    private Bitmap placeHolder;
    private int screen_size;
    private int image_division;

    public EnhancedSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, int screen_size, int image_division){
        super(context,layout,c,from,to,flags);
        this.screen_size = screen_size;
        this.image_division = image_division;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        final ViewBinder binder = mViewBinder;
        final int count = mTo.length;
        final int[] from = mFrom;
        final int[] to = mTo;

        for (int i = 0; i < count; i++){
            final View v = view.findViewById(to[i]);
            if(v != null){
                boolean bound = false;
                if(binder != null){
                    bound = binder.setViewValue(v, cursor, from[i]);
                }

                if(!bound){
                    String text = cursor.getString(from[i]);
                    if(text == null){
                        text = "";
                        if(to[i] == R.id.cursor_album_layout_album_image){
                            text = ""+DEFAUULT_IMAGE_RESOURCE;
                        }
//                    }else if(text.equals("<unknown>")){
//                        text = DEFAULT_UNKNOWN_VALUE;
                    }

                    if (v instanceof TextView){
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView){
                        ((ImageView)v).getLayoutParams().height = screen_size/3;
                        ((ImageView)v).getLayoutParams().width = screen_size/3;
                        setViewImage((ImageView) v, text);
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a view that can be bound by this EnhancedSimpleCursorAdapter");
                    }
                }
            }
        }

    }

    @Override
    public void setViewImage(ImageView v, String value){
        try{
            v.setImageResource(Integer.parseInt(value));
        }catch (NumberFormatException e){
            v.setImageURI(Uri.parse(value));
        }
        // Even after trying to resolve the Resource ID and the Uri of the album image
        // Create an AsyncTask to pull an image from the first song of the album
        if(v.getDrawable() == null){
            Cursor tempCursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DATA}, MediaStore.Audio.Media.ALBUM_ID + " = ?", new String[]{mCursor.getString(mFrom[0])}, null);
            if(tempCursor.moveToFirst()) {
                int pathCol = tempCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String path = tempCursor.getString(pathCol);
                loadBitmapImage(path, v);
                tempCursor.close();
            }else{
                // All else fricking failed, so just set the ImageView to the default image
                v.setImageResource(DEFAUULT_IMAGE_RESOURCE);
            }
        }
    }

    private void loadBitmapImage(String albummPath, ImageView albumImage){
        if(cancelPotentailWork(albummPath, albumImage)){
            final BitmapWorkerTask task = new BitmapWorkerTask(albumImage);
            placeHolder = ((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.no_album_image_v2)).getBitmap();
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), placeHolder, task);
            albumImage.setImageDrawable(asyncDrawable);
            task.execute(albummPath);
        }
    }

    private static class AsyncDrawable extends BitmapDrawable{
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
            super(res,bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask(){
            return bitmapWorkerTaskReference.get();
        }
    }

    private static boolean cancelPotentailWork(String data, ImageView imageView){
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if(bitmapWorkerTask != null){
            final String bitmapData = bitmapWorkerTask.data;
            if(bitmapData == null || !bitmapData.equals(data)){
                bitmapWorkerTask.cancel(true);
            }else{
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
        if(imageView != null){
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncDrawable){
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
        private final WeakReference<ImageView> imageViewReference;
        private String data = null;

        public BitmapWorkerTask(ImageView imageView){
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            MediaMetadataRetriever meta = new MediaMetadataRetriever();
            Uri dataUri = Uri.fromFile(new File(data));
            try{
                meta.setDataSource(mContext, dataUri);
            }catch(IllegalArgumentException iae){
                //meh
            }
            byte[] art = meta.getEmbeddedPicture();
            meta.release();
            if(art != null){
                return decodeSampledByteArray(art, screen_size/image_division, screen_size/image_division);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(imageViewReference != null && bitmap != null){
                final ImageView imageView = imageViewReference.get();
                if(imageView != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        public Bitmap decodeSampledByteArray(byte[] art, int reqHeight, int reqWidth){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(art, 0, art.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(art, 0, art.length, options);
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth){
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;

            if(height > reqHeight || width > reqWidth){
                int halfHeight = height/2;
                int halfWidth = width/2;

                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth){
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }


    }

}
