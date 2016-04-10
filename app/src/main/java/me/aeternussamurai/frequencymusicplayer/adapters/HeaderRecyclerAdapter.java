package me.aeternussamurai.frequencymusicplayer.adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.aeternussamurai.frequencymusicplayer.R;
import me.aeternussamurai.frequencymusicplayer.glide.MusicCover;

/**
 * Created by Chase on 4/9/2016.
 */
public class HeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int HEADER_TYPE = -1;
    private CursorRecyclerAdapter baseAdapter;

    private String headerInfo;
    private String headerImagePath;
    private Context context;

    private WindowTag tag;

    public HeaderRecyclerAdapter(Context context, CursorRecyclerAdapter baseAdapter, String headerInfo, String headerImagePath, WindowTag tag) {
        this.baseAdapter = baseAdapter;
        this.context = context;
        this.headerInfo = headerInfo;
        this.headerImagePath = headerImagePath;
        this.tag = tag;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.selection_list_header, parent, false);
            return new HeaderViewHolder(v, tag);
        } else {
            return baseAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPosition(position)) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.selectionInformation.setText(headerInfo);
            if(!headerImagePath.isEmpty()) {
                Glide.with(context)
                        .load(new MusicCover(headerImagePath))
                        .placeholder(R.drawable.no_album_image_v2)
                        .error(R.drawable.no_album_image_v2)
                        .into(h.selectionImage);
            }
        } else {
            baseAdapter.onBindViewHolder(holder, position-1);
        }
    }



    @Override
    public int getItemCount() {
        return baseAdapter.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return HEADER_TYPE;
        }
        return baseAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return isHeaderPosition(position) ? Integer.MAX_VALUE : baseAdapter.getItemId(position);
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Button shuffleSongs;
        public Button shuffleAlbums;
        public Button shuffleArtists;
        public FloatingActionButton playFromTop;

        public ImageView selectionImage;
        public TextView selectionInformation;

        public HeaderViewHolder(View itemView, WindowTag tag) {
            super(itemView);
            selectionImage = (ImageView) itemView.findViewById(R.id.selection_header_image);
            selectionInformation = (TextView) itemView.findViewById(R.id.selection_header_info);

            shuffleAlbums = (Button) itemView.findViewById(R.id.shuffle_album_button);
            shuffleSongs = (Button) itemView.findViewById(R.id.shuffle_song_button);
            shuffleArtists = (Button) itemView.findViewById(R.id.shuffle_artist_button);
            playFromTop = (FloatingActionButton) itemView.findViewById(R.id.fab);
            shuffleSongs.setOnClickListener(this);
            shuffleAlbums.setOnClickListener(this);
            shuffleArtists.setOnClickListener(this);
            playFromTop.setOnClickListener(this);
            if (tag == WindowTag.ARTIST) {
                shuffleAlbums.setVisibility(View.VISIBLE);
                shuffleArtists.setVisibility(View.GONE);
                shuffleSongs.setVisibility(View.GONE);
                shuffleSongs.setEnabled(false);
                shuffleAlbums.setEnabled(true);
                shuffleArtists.setEnabled(false);
            } else if (tag == WindowTag.ALBUM || tag == WindowTag.ARTIST_ALBUM) {
                shuffleSongs.setVisibility(View.VISIBLE);
                shuffleAlbums.setVisibility(View.GONE);
                shuffleArtists.setVisibility(View.GONE);
                shuffleSongs.setEnabled(true);
                shuffleAlbums.setEnabled(false);
                shuffleArtists.setEnabled(false);
            } else {
                shuffleSongs.setVisibility(View.VISIBLE);
                shuffleAlbums.setVisibility(View.VISIBLE);
                shuffleArtists.setVisibility(View.VISIBLE);
                shuffleSongs.setEnabled(true);
                shuffleAlbums.setEnabled(true);
                shuffleArtists.setEnabled(true);
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.shuffle_album_button:
                    // send request to music service to play all of the albums by the selected artist
                    break;
                case R.id.shuffle_artist_button:
                    // send request to music service to randomize the selection by artist
                    break;
                case R.id.shuffle_song_button:
                    // send request to music service to randomize the entire selection
                    break;
                case R.id.fab:
                    // send request to music service to play the selection from the top
                    break;
            }
        }
    }
}
