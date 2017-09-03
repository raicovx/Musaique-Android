package au.com.raicovtechnologyservices.musaique;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static au.com.raicovtechnologyservices.musaique.MainActivity.EXTERNAL_STORAGE_REQUEST_CODE;

/**
 * Created by Jamie on 10/04/2017.
 */

public class LibraryListFragment extends Fragment implements Runnable, RecyclerViewClickListener {

    private ArrayList<Song> songs;
    private RecyclerView mLibraryList;
    private RecyclerView.LayoutManager mLibraryLayoutManager;

    //Action Bar Media Control Declarations
    private CardView mediaControls;
    private ImageView npAlbumArt;
    private TextView npSongTitle;
    private TextView npArtistName;
    private TextView npAlbumTitle;
    private FloatingActionButton fab;

    //--Progress Bar
    private ProgressBar pb;
    private int currentPosition;

    //Animation Variables
    private int initialHeight;
    private int distanceToExpand;
    private int targetHeight;
    private boolean fabIsVisible = false;

    public CustomPlayer mediaPlayer;
    private File musicDirectory;

    //Animations
    private Animation fab_show;
    private Animation progress_expand;

    public LibraryListFragment() {
        //Required Public Constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        View rootView = inflater.inflate(R.layout.library_list_fragment, container, false);

        //declare song array
        songs = new ArrayList<Song>();


        //instantiate media player
        mediaPlayer = new CustomPlayer(getContext());

        fab = (FloatingActionButton)getActivity().findViewById(R.id.np_play_pause);
        pb = (ProgressBar) getActivity().findViewById(R.id.now_playing_music_progress);

        //Animations
        fab_show = AnimationUtils.loadAnimation(getContext(), R.anim.fab_show);
        progress_expand = AnimationUtils.loadAnimation(getContext(), R.anim.progress_expand);

        //Library List Stuff
        mLibraryList = (RecyclerView) rootView.findViewById(R.id.library_list_view);
        mLibraryLayoutManager = new LinearLayoutManager(getActivity());
        mLibraryList.setLayoutManager(mLibraryLayoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    } else {
                        mediaPlayer.start();
                        fab.setImageResource(R.drawable.ic_pause_white_48dp);
                    }
                }
            }
        });

        //populate list
        getSongData();
        return rootView;

    }

    private void getSongData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions();
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                runMusicQuery();
            }
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
        }
    }

    private void runMusicQuery(){
        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor musicCursor = musicResolver.query(musicUri,null,selection,null, null);
        musicCursor.moveToFirst();
        if(musicCursor != null) {
            //get Columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songPathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);


            //add songs to list
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                long albumId = musicCursor.getLong(albumArtColumn);
                String songPath =  musicCursor.getString(songPathColumn);
                songs.add(new Song(id, title, artist, album, albumId, songPath, getContext(), songs.size()));

            } while (musicCursor.moveToNext());
            mLibraryList.setAdapter(new LibraryListAdapter(songs, this));
        }
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Song selectedSong = songs.get(position);
        npAlbumArt = (ImageView)getActivity().findViewById(R.id.now_playing_album_art);
        npAlbumArt.setImageBitmap(selectedSong.getAlbumArt());

        npSongTitle = (TextView)getActivity().findViewById(R.id.now_playing_song_title);
        npSongTitle.setText(selectedSong.getTrackTitle());

        npArtistName = (TextView)getActivity().findViewById(R.id.now_playing_artist_name);
        npArtistName.setText(selectedSong.getArtistName());

        npAlbumTitle = (TextView)getActivity().findViewById(R.id.now_playing_album_title);
        npAlbumTitle.setText(selectedSong.getAlbumTitle());

        //Declare - Media Controls
        mediaControls = (CardView)getActivity().findViewById(R.id.now_playing_media_control_panel);

        //Animation - Media Controls
        initialHeight = mediaControls.getHeight();

        mediaControls.measure(ViewGroup.LayoutParams.MATCH_PARENT, 64);
        targetHeight = mediaControls.getMeasuredHeight();
        distanceToExpand = targetHeight - initialHeight;

        Animation expandToolbar = new Animation() {
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1){
                    // Do this after expanded
                }

                mediaControls.getLayoutParams().height = (int) (initialHeight + (distanceToExpand * interpolatedTime));
                mediaControls.requestLayout();


            }
        };


        expandToolbar.setDuration((long) distanceToExpand);
        mediaControls.startAnimation(expandToolbar);

        if(fab.getVisibility() != View.VISIBLE ) {
            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(fab_show);
            pb.setVisibility(View.VISIBLE);
            pb.startAnimation(progress_expand);
            fabIsVisible = true;
        }


        mediaPlayer.playSong(selectedSong.getSongPath(), getActivity());
        new Thread(this).start();

    }

    @Override
    public void run() {
        while(mediaPlayer != null && mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()){
            try{
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            pb.setProgress(currentPosition);

        }
    }
}
