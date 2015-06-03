package me.aeternussamurai.frequencymusicplayer.fragments;

import android.os.Bundle;
import android.app.Fragment;
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
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
