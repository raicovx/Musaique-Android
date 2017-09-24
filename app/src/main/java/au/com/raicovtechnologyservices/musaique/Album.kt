package au.com.raicovtechnologyservices.musaique

class Album(albumName: String){

    var albumTitle: String? = null;
    var albumSongs: ArrayList<Song>? = null;

    init{
        this.albumTitle = albumName
        this.albumSongs = ArrayList()

    }

    public fun addSongtoAlbum(song: Song){
        this.albumSongs!!.add(song)
    }
}
