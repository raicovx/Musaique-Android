package au.com.raicovtechnologyservices.musaique;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamie on 10/04/2017.
 */

public class LibraryListAdapter extends RecyclerView.Adapter<LibraryListAdapter.ViewHolder>{

    private ArrayList<Song> songData;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mSongTitle;
        public TextView mArtistName;
        public TextView mAlbumTitle;
        public ImageView mAlbumArt;
        public TextView mSongDuration;


        public ViewHolder(View v){
            super(v);
            mSongTitle = (TextView)v.findViewById(R.id.list_song_title);
            mArtistName = (TextView)v.findViewById(R.id.list_artist_name);
            mAlbumTitle = (TextView)v.findViewById(R.id.list_album_name);
            mAlbumArt = (ImageView)v.findViewById(R.id.album_art_iv);
        }
    }

    public LibraryListAdapter(ArrayList<Song> songData){
        this.songData = songData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.library_list_item,  parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSongTitle.setText(songData.get(position).getTrackTitle());
        holder.mAlbumTitle.setText(songData.get(position).getAlbumName());
        holder.mArtistName.setText(songData.get(position).getArtistName());
        holder.mAlbumArt.setImageBitmap(songData.get(position).getAlbumArt());
    }

    @Override
    public int getItemCount() {
        return songData.size();
    }
}
