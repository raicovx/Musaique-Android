package au.com.raicovtechnologyservices.musaique;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jamie on 10/04/2017.
 */

public class PlaylistListFragment extends Fragment {

    private int[] mImageResIds;
    private String[] mSongTitles;
    private String[] mArtistNames;
    private String[] mAlbunTitles;

    public PlaylistListFragment() {
        //Required Public Constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        return inflater.inflate(R.layout.playlist_list_fragment, container, false);
    }
}
