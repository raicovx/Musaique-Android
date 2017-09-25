package au.com.raicovtechnologyservices.musaique

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import org.w3c.dom.TypeInfo

import java.io.File
import kotlin.collections.ArrayList

/**
 * Created by Jamie on 10/04/2017.
 */

class LibraryListFragment : Fragment(), RecyclerViewClickListener {

    private var songs: ArrayList<Song>? = null
    private var mLibraryList: RecyclerView? = null
    private var mLibraryLayoutManager: RecyclerView.LayoutManager? = null
    private val EXTERNAL_STORAGE_REQUEST_CODE = 433

    //Action Bar Media Control Declarations
    private var mediaControls: CardView? = null
    private var fab: FloatingActionButton? = null

    //--Progress Bar
    private var pb: ProgressBar? = null
    private var currentPosition: Int = 0

    //Animation Variables
    private var initialHeight: Int = 0
    private var distanceToExpand: Int = 0
    private var targetHeight: Int = 0
    private var fabIsVisible = false

    var mMediaPlayer: CustomPlayer? = null
    private val musicDirectory: File? = null

    //Animations
    private var fab_show: Animation? = null
    private var progress_expand: Animation? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstance: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.library_list_fragment, container, false)

        //declare song array
        songs = mMediaPlayer!!.allSongsList


        //Declare - Media Controls
        mediaControls = activity.findViewById(R.id.now_playing_media_control_panel)


        fab = activity.findViewById(R.id.np_play_pause)
        pb = activity.findViewById(R.id.now_playing_music_progress)

        //Animations
        fab_show = AnimationUtils.loadAnimation(context, R.anim.fab_show)
        progress_expand = AnimationUtils.loadAnimation(context, R.anim.progress_expand)

        //Library List Stuff
        mLibraryList = rootView.findViewById(R.id.library_list_view)
        mLibraryLayoutManager = LinearLayoutManager(activity)
        mLibraryList!!.layoutManager = mLibraryLayoutManager

        //Set Library list adapter
        mLibraryList!!.adapter = LibraryListAdapter(songs as ArrayList<Song>, this)

        //populate list

        return rootView

    }
    //Media Player Transport for Fragments
    public fun setMediaPlayer(mediaPlayer:CustomPlayer){
        mMediaPlayer = mediaPlayer
        mMediaPlayer!!.currentFragment = this as Fragment
    }





    override fun recyclerViewListClicked(v: View, position: Int) {
        val selectedSong = songs!![position]


        //Animation - Media Controls
        initialHeight = mediaControls!!.height

        mediaControls!!.measure(ViewGroup.LayoutParams.MATCH_PARENT, 64)
        targetHeight = mediaControls!!.measuredHeight
        distanceToExpand = targetHeight - initialHeight

        val expandToolbar = object : Animation() {
            override fun willChangeBounds(): Boolean {
                return true
            }

            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    // Do this after expanded
                }

                mediaControls!!.layoutParams.height = (initialHeight + distanceToExpand * interpolatedTime).toInt()
                mediaControls!!.requestLayout()


            }
        }


        expandToolbar.duration = distanceToExpand.toLong()
        mediaControls!!.startAnimation(expandToolbar)

        if (fab!!.visibility != View.VISIBLE) {
            fab!!.visibility = View.VISIBLE
            fab!!.startAnimation(fab_show)
            pb!!.visibility = View.VISIBLE
            pb!!.startAnimation(progress_expand)
            fabIsVisible = true
        }


        mMediaPlayer!!.playSong(selectedSong, songs as ArrayList<Song>, position)

        mMediaPlayer?.let{ mediaPlayer -> mediaPlayer.setCurrentFragmentProgressBar(activity.findViewById(R.id.now_playing_music_progress))}

    }




}//Required Public Constructor
