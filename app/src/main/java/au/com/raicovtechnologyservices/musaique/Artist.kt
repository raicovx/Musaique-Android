package au.com.raicovtechnologyservices.musaique

class Artist(artistName: String){

    var artistName: String? = null
    var artistAlbums: ArrayList<Album>? = null;
    var artistSongs: ArrayList<Song>? = null

    init{
        this.artistName = artistName




    }

    public fun getArtistSongsAndAlbums(allSongsList: ArrayList<Song>) {
        artistSongs = ArrayList()
        artistAlbums = ArrayList()
        for(song: Song in allSongsList){
            when { //Check that Artist Matches
                song.artistName == this.artistName -> {

                    //filter array list to determine if the album exists or not
                    var album: List<Album>? = artistAlbums!!.filter{ album -> album.albumTitle == song.albumTitle}

                    if(album!!.isEmpty()){
                        var newAlbum: Album = Album(song.albumTitle)
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