package au.com.raicovtechnologyservices.musaique

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView.ScaleType
import android.widget.GridView
import android.widget.ImageView


class ArtistGridImageAdapter(c: Context, artistList:ArrayList<Artist>): BaseAdapter(){

    private var mContext: Context
    private var artistList: ArrayList<Artist>

    init{
            mContext = c
            this.artistList = artistList
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val imageView: ImageView
        if (p1 == null) {

            // if it's not recycled, initialize some attributes
            imageView = ImageView(mContext)
            imageView.scaleType = ScaleType.FIT_CENTER
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = p1 as ImageView
        }
//SET THE IMAGE HERE
        //imageView.setImageResource()
        return imageView
    }

    override fun getItem(p0: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemId(p0: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
       return artistList.size
    }

}
