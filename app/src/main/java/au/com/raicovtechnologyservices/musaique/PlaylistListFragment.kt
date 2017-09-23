package au.com.raicovtechnologyservices.musaique

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PlaylistListFragment : Fragment() {

    private val mImageResIds: IntArray? = null
    private val mSongTitles: Array<String>? = null
    private val mArtistNames: Array<String>? = null
    private val mAlbunTitles: Array<String>? = null

    var mMediaPlayer: CustomPlayer? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstance: Bundle?): View? {
        return inflater!!.inflate(R.layout.playlist_list_fragment, container, false)



    }

    fun setMediaPlayer(mediaPlayer: CustomPlayer){
        mMediaPlayer = mediaPlayer
    }

}//Required Public Constructor
