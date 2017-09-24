package au.com.raicovtechnologyservices.musaique

import android.support.v4.app.Fragment

class ArtistGridFragment : Fragment(){

    var mMediaPlayer: CustomPlayer? = null
    var artists: ArrayList<Artist>? = null


    public fun setMediaPlayer(mediaPlayer:CustomPlayer){
        this.mMediaPlayer = mediaPlayer
    }
}