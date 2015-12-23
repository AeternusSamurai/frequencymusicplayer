package me.aeternussamurai.frequencymusicplayer.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import me.aeternussamurai.frequencymusicplayer.MainActivity;
import me.aeternussamurai.frequencymusicplayer.R;

/**
 * Created by Chase on 5/31/2015.
 */
public class CurrentSongExtraFragment extends Fragment implements GestureDetector.OnGestureListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_song_extra, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // do some stuff here to set up the extra information.
        final GestureDetector detector = new GestureDetector(getActivity(), this);
        view.findViewById(R.id.cse_slide_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
    }


    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onDown: " + e.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("FMP_GESTURE_TEST", "onShowPress: " + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //TODO add in logic to determine where on the screen the user clicked. It should only "close" the extras view when the user doesn't tap on the extras view.
        getFragmentManager().popBackStack();
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

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("FMP_GESTURE_TEST", "onFling: " + e1.toString() + e2.toString());
        boolean result;
        float deltaX = Math.abs(e1.getX() - e2.getX());
        float deltaY = Math.abs(e1.getY() - e2.getY());
        if (deltaY > deltaX) {
            if (e1.getY() > e2.getY()) {
                // from the bottom to the top
                // do nothing really
                result = false;
            } else {
                // from the top to the bottom
                getFragmentManager().popBackStack();
                result = true;
            }
        } else {
            // Not a vertical swipe.
            result = false;
        }
        return result;
    }
}
