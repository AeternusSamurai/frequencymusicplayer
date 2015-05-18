package me.aeternussamurai.frequencymusicplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.aeternussamurai.frequencymusicplayer.R;

/**
 * Created by Chase on 5/17/2015.
 */
public class EnhancedSimpleCursorAdapter extends SimpleCursorAdapter {

    private final String DEFAULT_UNKNOWN_VALUE = "Unknown";
    private final int DEFAUULT_IMAGE_RESOURCE = R.drawable.no_album_image_v2;

    private ViewBinder mViewBinder;

    public EnhancedSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
        super(context,layout,c,from,to,flags);
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
                    }else if(text.equals("<unknown>")){
                        text = DEFAULT_UNKNOWN_VALUE;
                    }

                    if (v instanceof TextView){
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView){
                        setViewImage((ImageView) v, text);
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a view that can be bound by this EnhancedSimpleCursorAdapter");
                    }
                }
            }
        }

    }

}
