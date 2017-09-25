package au.com.raicovtechnologyservices.musaique

import android.Manifest
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.opengl.Visibility
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationBuilderWithBuilderAccessor
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.media.session.MediaSession
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import java.io.IOException

class CustomPlayer(var mContext: Context) : android.media.MediaPlayer(), Runnable {

    //Media Session
    public var mMediaSession: MediaSession = MediaSession(mContext, "Musaique")

    //UI Helper
    public lateinit var currentFragment: Fragment

    //Now Playing info
    private var npAlbumArt: ImageView? = null
    private var npSongTitle: TextView? = null
    private var npArtistName: TextView? = null
    private var npAlbumTitle: TextView? = null
    private var currentFragmentProgressBar: ProgressBar? = null;

    //Song Lists
    public var allSongsList: ArrayList<Song>? = null
    public var currentPlaylist: ArrayList<Song>? = null
    public var artistList: ArrayList<Artist>? = null
    private var currentPos: Int = 0

    //Intents
    val KEY_PREV: String ="au.com.raicovtechnologyservices.musaique.CustomPlayer.prevSong"
    val pendingIntent: PendingIntent
        get() {
            val openMainIntent = Intent(this.mContext, MainActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(mContext)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(openMainIntent)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }

    val prevIntent: Intent = Intent(KEY_PREV)
    val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(mContext, 0, prevIntent, 0)
    init {

        allSongsList = ArrayList()
        artistList = ArrayList()


        findAllSongs()

    }

    fun playSong(song:Song, songs:ArrayList<Song>, position: Int): Boolean {
        try {
            if(this.isPlaying) {
                this.stop()
            }
            this.reset()
            this.setDataSource(song.songPath)
            this.prepareAsync()
            this.currentPlaylist = songs
            this.currentPos = position

            this.setOnPreparedListener { mp ->
                mp.start()
                createNotificationControls(currentPlaylist!![currentPos].trackTitle+" - "+ currentPlaylist!![currentPos].artistName, currentPlaylist!![currentPos].albumTitle)
                updateFragmentUI(currentFragment.activity)
            }

            this.setOnCompletionListener { mp -> mp.reset() }
            this.setOnErrorListener { mp, what, extra ->

                mp.reset()
                false
            }

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        }


        return true
    }

    public fun prevSong(){
        this.stop()
        this.reset()
        when(currentPos) {
            0 -> {
                this.setDataSource(this.currentPlaylist!![(this.currentPlaylist!!.size) - 1].songPath)
                this.currentPos = this.currentPlaylist!!.size - 1
            }
            else -> {
                this.setDataSource(this.currentPlaylist!![this.currentPos - 1].songPath)
                this.currentPos = this.currentPos - 1
            }
        }

        this.prepareAsync()


    }



    public fun updateFragmentUI(activity: Activity) {

            npAlbumArt = activity.findViewById(R.id.now_playing_album_art)
            npAlbumArt!!.setImageBitmap(currentPlaylist!![currentPos].albumArt)

            npSongTitle = activity.findViewById(R.id.now_playing_song_title)
            npSongTitle!!.text = currentPlaylist!![currentPos].trackTitle

            npArtistName = activity.findViewById(R.id.now_playing_artist_name)
            npArtistName!!.text = currentPlaylist!![currentPos].artistName

            npAlbumTitle = activity.findViewById(R.id.now_playing_album_title)
            npAlbumTitle!!.text = currentPlaylist!![currentPos].albumTitle

            val fab: FloatingActionButton = activity.findViewById(R.id.np_play_pause)
            fab.setImageResource(R.drawable.ic_pause_white_48dp)
            fab.isClickable = true
            val pb: ProgressBar = activity.findViewById(R.id.now_playing_music_progress)
            pb.progress = 0
            pb.max = this.duration
            Thread(this).start()

    }

    public fun getAllSongs(): ArrayList<Song>?{
        return allSongsList;
    }

    //Get All Songs List
    private fun findAllSongs() {
        val musicResolver = mContext.contentResolver
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
                allSongsList!!.add(Song(id, title, artist, album, albumId, songPath, mContext, allSongsList!!.size))

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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationControls(notificationTitleString: String, notificationContentString: String) {
        val channelId: String = "musaique_channel"
        val mBuilder: Notification.Builder = Notification.Builder(this.mContext)
                .setSmallIcon(R.drawable.ic_headphone)
                .setContentTitle(notificationTitleString)
                .setContentText(notificationContentString)
                .setOngoing(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setStyle(Notification.MediaStyle().setMediaSession(mMediaSession.sessionToken).setShowActionsInCompactView(0))
                .addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent) //TODO replace with AddAction(Action)




        val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notification: Notification = mBuilder.build()


        notification.flags = Notification.FLAG_NO_CLEAR
        mNotificationManager.notify(1, notification)
        }


    override fun run() {
        while (this != null && this.isPlaying && this.currentPosition < this.duration) {
            try {
                Thread.sleep(1000)

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            currentFragmentProgressBar!!.progress = this.currentPosition

        }
    }

    public fun setCurrentFragmentProgressBar(now_playing_music_progress: ProgressBar?) {
        this.currentFragmentProgressBar = now_playing_music_progress
    }
}
