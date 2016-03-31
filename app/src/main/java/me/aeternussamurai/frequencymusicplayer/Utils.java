package me.aeternussamurai.frequencymusicplayer;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by Chase on 3/29/2016.
 */
public class Utils {

    public static int getToolbarHeight(Context context){
        final TypedArray styleAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styleAttributes.getDimension(0,0);
        styleAttributes.recycle();

        return toolbarHeight;
    }
}
