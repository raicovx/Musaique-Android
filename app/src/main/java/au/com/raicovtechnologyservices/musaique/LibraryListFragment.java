package au.com.raicovtechnologyservices.musaique;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import static au.com.raicovtechnologyservices.musaique.MainActivity.EXTERNAL_STORAGE_REQUEST_CODE;

/**
 * Created by Jamie on 10/04/2017.
 */

public class LibraryListFragment extends Fragment implements RecyclerViewClickListener {

    private ArrayList<Song> songs;
    private RecyclerView mLibraryList;
    private RecyclerView.LayoutManager mLibraryLayoutManager;

    public CustomPlayer mediaPlayer;
    private File musicDirectory;


    public LibraryListFragment() {
        //Required Public Constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        View rootView = inflater.inflate(R.layout.library_list_fragment, container, false);

        //declare song array and populate array
        songs = new ArrayList<Song>();
        getSongData();

        //instantiate media player
        mediaPlayer = new CustomPlayer();

        //Library List Stuff
        mLibraryList = (RecyclerView) rootView.findViewById(R.id.library_list_view);
        mLibraryLayoutManager = new LinearLayoutManager(getActivity());
        mLibraryList.setLayoutManager(mLibraryLayoutManager);
        return rootView;

    }

    private void getSongData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions();
        }else{
            runMusicQuery();
        }
    }

    @TargetApi(23)
    private void createPermissions() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)){
                requestPermissions(new String[]{permission}, EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }else{
            runMusicQuery();
        }
    }

    private void runMusicQuery(){
        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null, null);

        if(musicCursor != null && musicCursor.moveToFirst()) {
            //get Columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            String songPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));


            //add songs to list
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                long albumId = musicCursor.getLong(albumArtColumn);
                String duration = musicCursor.getString(durationColumn);
                songs.add(new Song(id, title, artist, album, albumId, duration, songPath, getContext(), songs.size()));

            } while (musicCursor.moveToNext());
            mLibraryList.setAdapter(new LibraryListAdapter(songs, this));
        }
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        mediaPlayer.playSong(songs.get(position).getSongPath());
    }
}
