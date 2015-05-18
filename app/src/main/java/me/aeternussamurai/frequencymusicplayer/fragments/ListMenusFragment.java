package me.aeternussamurai.frequencymusicplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import me.aeternussamurai.frequencymusicplayer.R;

/**
 * Created by Chase on 5/16/2015.
 */
public class ListMenusFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListAdapter adapter;

    public static ListMenusFragment newInstance(int sectionNumber){
        ListMenusFragment fragment = new ListMenusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAdapter(ListAdapter ad){
        adapter = ad;
    }

    public ListMenusFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container,false);
        ListView listing = (ListView)rootView.findViewById(R.id.listing);
        GridView grid = (GridView) rootView.findViewById(R.id.album_grid);
        TextView testText = (TextView) rootView.findViewById(R.id.test_text);
        testText.setText("Section " + this.getArguments().getInt(ARG_SECTION_NUMBER));
        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
            listing.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
            grid.setAdapter(adapter);
        } else {
            grid.setVisibility(View.GONE);
            listing.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

}
