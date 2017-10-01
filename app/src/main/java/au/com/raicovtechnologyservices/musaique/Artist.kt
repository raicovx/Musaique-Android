package au.com.raicovtechnologyservices.musaique

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaBrowserCompat

class Artist(artistName: String){

    var artistName: String? = null
    var artistAlbums: ArrayList<Album>? = null;
    var artistSongs: ArrayList<MediaBrowserCompat.MediaItem>? = null

    init{
        this.artistName = artistName




    }

    public fun getArtistSongsAndAlbums(allSongsList: ArrayList<MediaBrowserCompat.MediaItem>) {
        artistSongs = ArrayList()
        artistAlbums = ArrayList()
        for(song: MediaBrowserCompat.MediaItem in allSongsList){
            when { //Check that Artist Matches
                song.description.extras!!["artist"] == this.artistName -> {
                    var albumTitle:String = song.description.extras!!.getString("album")
                    //filter array list to determine if the album exists or not
                    var album: List<Album>? = artistAlbums!!.filter{ album -> album.albumTitle == albumTitle}

                    if(album!!.isEmpty()){
                        var newAlbum: Album = Album(albumTitle)
                        newAlbum.addSongtoAlbum(song)
                        artistAlbums!!.add(newAlbum)

                    }else{
                        var existingAlbum = album[0]
                        existingAlbum.addSongtoAlbum(song)
                    }

                    artistSongs!!.add(song)
                }
            }
        }


    }
}