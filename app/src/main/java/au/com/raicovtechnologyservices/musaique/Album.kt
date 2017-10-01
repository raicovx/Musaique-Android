package au.com.raicovtechnologyservices.musaique

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaBrowserCompat

class Album(albumName: String){

    var albumTitle: String? = null;
    var albumSongs: ArrayList<MediaBrowserCompat.MediaItem>? = null;

    init{
        this.albumTitle = albumName
        this.albumSongs = ArrayList()

    }

    public fun addSongtoAlbum(song: MediaBrowserCompat.MediaItem){
        this.albumSongs!!.add(song)
    }
}
