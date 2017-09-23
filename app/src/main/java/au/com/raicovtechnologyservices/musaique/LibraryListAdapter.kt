package au.com.raicovtechnologyservices.musaique

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import java.util.ArrayList


class LibraryListAdapter(private val songData: ArrayList<Song>, listener: RecyclerViewClickListener) : RecyclerView.Adapter<LibraryListAdapter.ViewHolder>() {

    var listener: RecyclerViewClickListener

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var mSongTitle: TextView
        var mArtistName: TextView
        var mAlbumTitle: TextView
        var mAlbumArt: ImageView

        init {
            mSongTitle = v.findViewById(R.id.list_song_title)
            mArtistName = v.findViewById(R.id.list_artist_name)
            mAlbumTitle = v.findViewById(R.id.list_album_name)
            mAlbumArt = v.findViewById(R.id.album_art_iv)
            v.setOnClickListener(this)


        }

        override fun onClick(v: View) {
            listener.recyclerViewListClicked(v, this.layoutPosition)
        }
    }

    init{
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.library_list_item, parent, false) as LinearLayout

        val vh = ViewHolder(v)
        return vh

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mSongTitle.text = songData[position].trackTitle
        holder.mAlbumTitle.text = songData[position].albumTitle
        holder.mArtistName.text = songData[position].artistName
        holder.mAlbumArt.setImageBitmap(songData[position].albumArt)
    }

    override fun getItemCount(): Int {
        return songData.size
    }

}
