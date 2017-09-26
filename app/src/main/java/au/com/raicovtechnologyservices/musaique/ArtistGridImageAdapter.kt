package au.com.raicovtechnologyservices.musaique

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView.ScaleType
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView


class ArtistGridImageAdapter(c: Context, artistList:ArrayList<Artist>): BaseAdapter(){

    private var mContext: Context
    private var artistList: ArrayList<Artist>

    init{
            mContext = c
            this.artistList = artistList
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var item: View
        var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (p1 == null) {// if it's not recycled, initialize some attributes
            //Set the custom layout
            item = View(mContext)
            item = inflater.inflate(R.layout.artist_grid_item, null)

            //Get references to all parts of an Item
            var tv: TextView = item.findViewById(R.id.artist_item_title)
            var iv: ImageView = item.findViewById(R.id.artist_image)

            tv.text = artistList[p0].artistName
            iv.setImageResource(R.drawable.music_placeholder)

        } else {
            item = p1  //if it already exists it hasnt been disposed of yet
        }

        return item
    }

    override fun getItem(p0: Int): Any {
        return artistList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
       return artistList.size
    }

}
