package au.com.raicovtechnologyservices.musaique;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jamie on 10/04/2017.
 */

public class LibraryListFragment extends Fragment {

    private ArrayList<Song> songs;
    public RecyclerView mLibraryList;
    public RecyclerView.LayoutManager mLibraryLayoutManager;
    private File musicDirectory;


    public LibraryListFragment() {
        //Required Public Constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        View rootView = inflater.inflate(R.layout.library_list_fragment, container, false);

        //Library List Stuff
        mLibraryList = (RecyclerView) rootView.findViewById(R.id.library_list_view);
        mLibraryLayoutManager = new LinearLayoutManager(getActivity());
        mLibraryList.setLayoutManager(mLibraryLayoutManager);
        getSongData();
        return rootView;

    }

    private void getSongData() {
       /* musicDirectory = new File( "/mnt/sdcard", "Music");
        File[] songFiles = musicDirectory.listFiles();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (songFiles != null) {
            for (int i = 0; i < songFiles.length; i++) {
                mmr.setDataSource(songFiles[i].getPath());
                byte[] data = mmr.getEmbeddedPicture();
                Bitmap albumArt;
                if (data != null) {
                    albumArt = BitmapFactory.decodeByteArray(data, 0, data.length);
                } else {
                    albumArt = null;
                }
                new Song(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM), mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), albumArt);
            }
            mLibraryList.setAdapter(new LibraryListAdapter(songs));
        }*/

        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null, null);

        if(musicCursor != null && musicCursor.moveToFirst()){
            //get Columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            //add songs to list
            do{
               long id = musicCursor.getLong(idColumn);
               String title = musicCursor.getString(titleColumn);
               String artist = musicCursor.getString(artistColumn);
               String album = musicCursor.getString(albumColumn);
               long albumId = musicCursor.getLong(albumArtColumn);
               String duration = musicCursor.getString(durationColumn);
               songs.add(new Song(id, title, artist, album, albumId, duration, getContext()));

            }while(musicCursor.moveToNext());
            mLibraryList.setAdapter(new LibraryListAdapter(songs));
        }
    }
}
