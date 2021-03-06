package au.com.raicovtechnologyservices.musaique

import android.Manifest
import android.annotation.TargetApi
import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
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
import android.net.Uri
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.*
import java.io.File

import java.io.IOException

class CustomPlayer(var mContext: Context) : android.media.MediaPlayer(), Runnable {


    //UI Helper
    public lateinit var currentFragment: Fragment

    //Now Playing info
    private var npAlbumArt: ImageView? = null
    private var npSongTitle: TextView? = null
    private var npArtistName: TextView? = null
    private var npAlbumTitle: TextView? = null
    private var currentFragmentProgressBar: ProgressBar? = null;

    //Song Lists
    public var allSongsList: ArrayList<MediaBrowserCompat.MediaItem>? = null
    public var currentPlaylist: ArrayList<MediaBrowserCompat.MediaItem>? = null
    public var artistList: ArrayList<Artist>? = null
    private var currentPos: Int = 0

    //Intents

    val pendingIntent: PendingIntent
        get() {
            val openMainIntent = Intent(this.mContext, MainActivity::class.java)
            openMainIntent.putExtra("mediaPlayer", ArrayList<CustomPlayer>().add(this))
            val stackBuilder = TaskStackBuilder.create(mContext)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(openMainIntent)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }
    //Previous Broadcast decs
    val KEY_PREV: String ="prevSong"
    val prevIntent: Intent = Intent(KEY_PREV)
    val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(mContext, 0, prevIntent, 0)

    //Play Broadcast decs
    val KEY_PLAY = "resumeSong"
    val playIntent: Intent = Intent(KEY_PLAY)
    val playPendingIntent: PendingIntent = PendingIntent.getBroadcast(mContext, 0, playIntent, 0)

    //Pause Broadcast Decs
    val KEY_PAUSE = "pauseSong"
    val pauseIntent: Intent = Intent(KEY_PAUSE)
    val pausePendingIntent: PendingIntent = PendingIntent.getBroadcast(mContext, 0, pauseIntent, 0)

    //Next Broadcast Decs
    val KEY_NEXT: String = "nextSong"
    val nextIntent: Intent = Intent(KEY_NEXT)
    val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(mContext, 0, nextIntent,0)
    init {

        allSongsList = ArrayList()
        artistList = ArrayList()


    }

    //Playback functions
    fun playSong(song:MediaBrowserCompat.MediaItem, songs:ArrayList<MediaBrowserCompat.MediaItem>, position: Int): Boolean {
        try {
            if(this.isPlaying) {
                this.stop()
            }
            this.reset()
            this.setDataSource(mContext, song.description.mediaUri)
            this.setWakeMode(mContext, android.os.PowerManager.PARTIAL_WAKE_LOCK)
            this.prepareAsync()
            this.currentPlaylist = songs
            this.currentPos = position

            this.setOnPreparedListener { mp ->
                mp.start()
                createNotificationControls()
                updateFragmentUI(currentFragment.activity)
            }

            this.setOnCompletionListener { mp -> nextSong() }
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

    fun resumeSong(){
        this.start()
        createNotificationControls()
    }

    fun pauseSong(){
        this.pause()
        createNotificationControls()
    }

    fun prevSong(){
        this.reset()
        when(currentPos) {
            0 -> {
                this.setDataSource(mContext, this.currentPlaylist!![(this.currentPlaylist!!.size) - 1].description.mediaUri)
                this.currentPos = this.currentPlaylist!!.size - 1
            }
            else -> {
                this.setDataSource(mContext, this.currentPlaylist!![this.currentPos - 1].description.mediaUri)
                this.currentPos = this.currentPos - 1
            }
        }

        this.prepareAsync()


    }
    fun nextSong(){
        this.reset()
        when(currentPos) {
            (this.currentPlaylist!!.size - 1) -> {
                this.currentPos = 0
                this.setDataSource(mContext, this.currentPlaylist!![0].description.mediaUri)
            }
            else -> {
                this.setDataSource(mContext, this.currentPlaylist!![this.currentPos + 1].description.mediaUri)
                this.currentPos = this.currentPos + 1
            }
        }

        this.prepareAsync()


    }

    //UI functions

    public fun updateFragmentUI(activity: Activity) {

            npAlbumArt = activity.findViewById(R.id.now_playing_album_art)
            npAlbumArt!!.setImageBitmap(currentPlaylist!![currentPos].description.iconBitmap)

            npSongTitle = activity.findViewById(R.id.now_playing_song_title)
            npSongTitle!!.text = currentPlaylist!![currentPos].description.title

            npArtistName = activity.findViewById(R.id.now_playing_artist_name)
            npArtistName!!.text = currentPlaylist!![currentPos].description.extras!!.getString("artist")

            npAlbumTitle = activity.findViewById(R.id.now_playing_album_title)
            npAlbumTitle!!.text = currentPlaylist!![currentPos].description.extras!!.getString("album")

            val fab: FloatingActionButton = activity.findViewById(R.id.np_play_pause)
            fab.setImageResource(R.drawable.ic_pause_white_48dp)
            fab.isClickable = true
            val pb: ProgressBar = activity.findViewById(R.id.now_playing_music_progress)
            pb.progress = 0
            pb.max = this.duration
            Thread(this).start()

    }



    public fun getAllSongs(): ArrayList<MediaBrowserCompat.MediaItem>?{
        findAllSongs()
        return allSongsList
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationControls() {
        val channelId: String = "musaique_channel"
        var remoteViews:RemoteViews = RemoteViews("au.com.raicovtechnologyservices.musaique", R.layout.music_notification_controls)
        //Text
        remoteViews.setTextViewText(R.id.notification_song_title, currentPlaylist!![currentPos].description.title)
        remoteViews.setTextViewText(R.id.notification_artist_name, currentPlaylist!![currentPos].description.extras!!.getString("artist"))
        remoteViews.setTextViewText(R.id.notification_album_title, currentPlaylist!![currentPos].description.extras!!.getString("album"))
        remoteViews.setImageViewBitmap(R.id.notification_album_art, currentPlaylist!![currentPos].description.iconBitmap)
        //Buttons

        when(this.isPlaying){
            true -> {
                remoteViews.setOnClickPendingIntent(R.id.notification_prev_button, prevPendingIntent)
                remoteViews.setViewVisibility(R.id.notification_play_button, View.GONE)
                remoteViews.setViewVisibility(R.id.notification_pause_button, View.VISIBLE)
                remoteViews.setOnClickPendingIntent(R.id.notification_pause_button, pausePendingIntent)
                remoteViews.setOnClickPendingIntent(R.id.notification_next_button, nextPendingIntent)
            }

            else ->{
                remoteViews.setOnClickPendingIntent(R.id.notification_prev_button, prevPendingIntent)
                remoteViews.setViewVisibility(R.id.notification_pause_button, View.GONE)
                remoteViews.setViewVisibility(R.id.notification_play_button, View.VISIBLE)
                remoteViews.setOnClickPendingIntent(R.id.notification_play_button, playPendingIntent)
                remoteViews.setOnClickPendingIntent(R.id.notification_next_button, nextPendingIntent)
            }

        }

        val mBuilder: Notification.Builder = Notification.Builder(this.mContext, channelId)
                .setSmallIcon(R.drawable.ic_headphone)
                .setColorized(true)
                .setColor(Color.argb(255,57,62,70))
                .setOngoing(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_STOP))
                        //.setShowActionsInCompactView(0, 1, 2))
                .setCustomContentView(remoteViews)


        //TODO replace these with AddAction(Action)
       /* when(this.isPlaying) {
            true -> {
                mBuilder.addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent)
                mBuilder.addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
                mBuilder.addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
            }
            else->{
                mBuilder.addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent)
                mBuilder.addAction(R.drawable.ic_play_arrow, "Play", playPendingIntent)
                mBuilder.addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
            }

        }*/


        val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notification: Notification = mBuilder.build()

        notification.flags = Notification.FLAG_NO_CLEAR
        mNotificationManager.notify(1, notification)
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
                val id:Long = musicCursor.getLong(idColumn)
                val title = musicCursor.getString(titleColumn)
                val artist = musicCursor.getString(artistColumn)
                val album = musicCursor.getString(albumColumn)
                val albumId = musicCursor.getLong(albumArtColumn)
                val songPath = musicCursor.getString(songPathColumn)
                var artUri: Uri? = null
                var albumArtBm: Bitmap? = null


                try {
                    val sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart")

                    artUri = ContentUris.withAppendedId(sArtworkUri, albumId)

                    val pfd = mContext.contentResolver
                            .openFileDescriptor(artUri, "r")

                    if (pfd != null) {
                        val fd = pfd.fileDescriptor

                        albumArtBm = BitmapFactory.decodeFileDescriptor(fd)
                    }
                } catch (e: Exception) {
                }

                var extras: Bundle = Bundle()
                extras.putString("artist", artist)
                extras.putString("album", album)
                var songFile = File(songPath)
                var mMediaDescription = MediaDescriptionCompat.Builder()
                        .setTitle(title)
                        .setMediaUri(Uri.fromFile(songFile))
                        .setIconUri(artUri)
                        .setMediaId(allSongsList!!.size.toString())
                        .setIconBitmap(albumArtBm)
                        .setExtras(extras)
                        .build()
                var mMediaItem = MediaBrowserCompat.MediaItem(mMediaDescription, MediaBrowser.MediaItem.FLAG_PLAYABLE)
                allSongsList!!.add(mMediaItem)

                var artistFilter: List<Artist> = artistList!!.filter{currArtist -> currArtist.artistName == artist as String}
                if(artistFilter.isEmpty()) {
                    artistList!!.add(Artist(artist))
                }
            } while (musicCursor.moveToNext())

            for(artist: Artist in artistList as ArrayList<Artist>){
                artist.getArtistSongsAndAlbums(allSongsList as ArrayList<MediaBrowserCompat.MediaItem>)
            }
        }else{
            //TODO: Display feedback to inform the user no music could be found.
        }

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

    fun setCurrentFragmentProgressBar(now_playing_music_progress: ProgressBar?) {
        this.currentFragmentProgressBar = now_playing_music_progress
    }
}
