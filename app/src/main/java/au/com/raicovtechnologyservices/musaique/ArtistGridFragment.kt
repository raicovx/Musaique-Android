package au.com.raicovtechnologyservices.musaique

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout

class ArtistGridFragment : Fragment(){

    var mMediaPlayer: CustomPlayer? = null
    var artists: ArrayList<Artist>? = null
    var artistsGrid: GridView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.artist_grid_fragment, container, false)

        artistsGrid = rootView.findViewById(R.id.artist_grid_view)

        artistsGrid!!.adapter = ArtistGridImageAdapter(mMediaPlayer!!.mContext, mMediaPlayer!!.artistList as ArrayList<Artist>)

        return rootView
    }



    public fun setMediaPlayer(mediaPlayer:CustomPlayer){
        this.mMediaPlayer = mediaPlayer
        mMediaPlayer!!.currentFragment = this
    }
}