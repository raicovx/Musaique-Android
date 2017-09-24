package au.com.raicovtechnologyservices.musaique

import android.Manifest
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import java.io.IOException

class CustomPlayer(var mContext: Context) : android.media.MediaPlayer(), Runnable {

    private var npAlbumArt: ImageView? = null
    private var npSongTitle: TextView? = null
    private var npArtistName: TextView? = null
    private var npAlbumTitle: TextView? = null
    private var currentFragmentProgressBar: ProgressBar? = null;
    //Song Lists
    private var allSongsList: ArrayList<Song>? = null
    private var currentPlaylist: ArrayList<Song>? = null
    private var artistList: ArrayList<Artist>? = null
    private var currentPos: Int = 0
    val pendingIntent: PendingIntent
        get() {
            val openMainIntent = Intent(this.mContext, MainActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(mContext)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(openMainIntent)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }


    init {

        allSongsList = ArrayList()
        artistList = ArrayList()
        findAllSongs()
    }

    fun playSong(song:Song, songs:ArrayList<Song>, position: Int,  activity: Activity): Boolean {
        try {
            if(this.isPlaying) {
                this.stop()
            }
            this.reset()
            this.setDataSource(song.songPath)
            this.prepare()
            this.currentPlaylist = songs;
            this.currentPos = position

            npAlbumArt = activity.findViewById(R.id.now_playing_album_art)
            npAlbumArt!!.setImageBitmap(song.albumArt)

            npSongTitle = activity.findViewById(R.id.now_playing_song_title)
            npSongTitle!!.text = song.trackTitle

            npArtistName = activity.findViewById(R.id.now_playing_artist_name)
            npArtistName!!.text = song.artistName

            npAlbumTitle = activity.findViewById(R.id.now_playing_album_title)
            npAlbumTitle!!.text = song.albumTitle

            this.setOnPreparedListener { mp ->
                mp.start()
                val fab: FloatingActionButton = activity.findViewById(R.id.np_play_pause)
                fab.setImageResource(R.drawable.ic_pause_white_48dp)
                fab.isClickable = true
                val pb: ProgressBar = activity.findViewById(R.id.now_playing_music_progress)
                pb.progress = 0
                pb.max = mp.duration
                Thread(this).start()
                createNotificationControls(song.trackTitle+" - "+ song.artistName, song.albumTitle)
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


    fun createNotificationControls(notificationTitleString: String, notificationContentString: String){
        val channelId: String = "musaique_channel"

        if(Build.VERSION.SDK_INT > 25) {
            createNotificationControlsWithChannel(notificationTitleString, notificationContentString, channelId)

        }else{
            val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_headphone)
                    .setContentTitle(notificationTitleString)
                    .setContentText(notificationContentString)
        }
    }
    @TargetApi(26)
    private fun createNotificationControlsWithChannel(notificationTitleString: String, notificationContentString: String, CHANNEL_ID: String) {

        val mBuilder: Notification.Builder = Notification.Builder(this.mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headphone)
                .setContentTitle(notificationTitleString)
                .setContentText(notificationContentString)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

               mNotificationManager.notify(1, mBuilder.build())
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
