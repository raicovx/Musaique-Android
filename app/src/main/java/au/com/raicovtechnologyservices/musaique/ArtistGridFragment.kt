package au.com.raicovtechnologyservices.musaique

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView

class ArtistGridFragment : Fragment(){

    var mMediaPlayer: CustomPlayer? = null
    var artists: ArrayList<Artist>? = null
    var artistsGrid: GridView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.artist_grid_fragment, container, false)

        artistsGrid = activity.findViewById(R.id.artist_grid_view)


        return rootView
    }

    public fun setMediaPlayer(mediaPlayer:CustomPlayer){
        this.mMediaPlayer = mediaPlayer
    }
}