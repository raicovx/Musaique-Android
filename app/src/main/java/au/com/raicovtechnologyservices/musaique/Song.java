package au.com.raicovtechnologyservices.musaique;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;

/**
 * Created by Jamie on 10/04/2017.
 */

public class Song {

    private String trackTitle;
    private String artistName;
    private String albumName;
    private String duration;
    private String songPath;
    private long id;
    private long albumId;
    private Context context;



    private int position;

    public Song(long id, String trackTitle, String artistName, String albumName, long albumId, String duration, String songPath, Context context, int position){
        this.id = id;
        this.trackTitle = trackTitle;
        this.artistName = artistName;
        this.albumName = albumName;
        this.albumId = albumId;
        this.duration = duration;
        this.context = context;
        this.songPath = songPath;
        this.position = position;
        getAlbumArt();
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongPath() { return songPath; }

    public String getAlbumName() {
        return albumName;
    }

    public String getDuration() {
        return duration;
    }

    public Bitmap getAlbumArt()
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, this.albumId);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    public int getPosition() {
        return position;
    }
}
