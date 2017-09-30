package au.com.raicovtechnologyservices.musaique

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import java.io.File

/**
 * Created by Jamie on 30/09/2017.
 */
class MediaPlaybackService: MediaBrowserServiceCompat() {


    private lateinit var mMediaSession: MediaSessionCompat
    private lateinit var mStateBuilder: PlaybackStateCompat.Builder

    //Lists
    private var allSongsList: ArrayList<MediaBrowserCompat.MediaItem>? = ArrayList<MediaBrowserCompat.MediaItem>()
    private var currentPlaylist:  ArrayList<MediaBrowserCompat.MediaItem>? = ArrayList<MediaBrowserCompat.MediaItem>()
    private var artistList:  ArrayList<Artist>? = ArrayList<Artist>()

    override fun onCreate() {
        super.onCreate()

        mMediaSession = MediaSessionCompat(applicationContext, "MusaiquePlaybackService")

        //Enable callbacks from media buttons and transport controls
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS and MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        //set initial playback state
        mStateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY and PlaybackStateCompat.ACTION_PLAY_PAUSE)
        mMediaSession.setPlaybackState(mStateBuilder.build())

        //Handle callbacks from the media controller
        mMediaSession.setCallback(MySessionCallback())

        //Set token to allow client activities to communicate with it
        sessionToken = mMediaSession.sessionToken

    }



    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
       result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    //Get All Songs List
    private fun findAllSongs() {
        val musicResolver = applicationContext.contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val musicCursor = musicResolver.query(musicUri, null, selection, null, null)
        if(musicCursor.count > 0) {
            musicCursor!!.moveToFirst()
            //get Columns
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val songPathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)


            //add songs to list
            do {
                val id = musicCursor.getLong(idColumn)
                val title = musicCursor.getString(titleColumn)
                val artist = musicCursor.getString(artistColumn)
                val album = musicCursor.getString(albumColumn)
                val albumId = musicCursor.getLong(albumArtColumn)
                val songPath = musicCursor.getString(songPathColumn)
                var artUri: Uri? = null
                try {
                    val sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart")

                    artUri = ContentUris.withAppendedId(sArtworkUri, albumId)


                } catch (e: Exception) {
                }
                var songFile = File(songPath)
                var mMediaDescription = MediaDescriptionCompat(allSongsList!!.size as String, title, artist, album, null, artUri, null, Uri.fromFile(songFile))
                var mMediaItem = MediaBrowserCompat.MediaItem(mMediaDescription, MediaBrowser.MediaItem.FLAG_PLAYABLE)
                allSongsList!!.add(mMediaItem)

                var artistFilter: List<Artist> = artistList!!.filter{currArtist -> currArtist.artistName == artist as String}
                if(artistFilter.isEmpty()) {
                    artistList!!.add(Artist(artist))
                }
            } while (musicCursor.moveToNext())

            for(artist: Artist in artistList as ArrayList<Artist>){
                artist.getArtistSongsAndAlbums(allSongsList as ArrayList<Song>)
            }
        }else{
            //TODO: Display feedback to inform the user no music could be found.
        }

    }


}