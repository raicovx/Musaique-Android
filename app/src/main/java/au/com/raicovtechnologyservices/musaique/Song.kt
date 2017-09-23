package au.com.raicovtechnologyservices.musaique

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.ParcelFileDescriptor

import java.io.FileDescriptor


class Song(private val id: Long, val trackTitle: String, val artistName: String, val albumTitle: String, private val albumId: Long, val songPath: String, private val context: Context, private val position: Int) {

    init {
        albumArt
    }


    val albumArt: Bitmap?
        get() {
            var bm: Bitmap? = null
            try {
                val sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart")

                val uri = ContentUris.withAppendedId(sArtworkUri, this.albumId)

                val pfd = context.contentResolver
                        .openFileDescriptor(uri, "r")

                if (pfd != null) {
                    val fd = pfd.fileDescriptor
                    bm = BitmapFactory.decodeFileDescriptor(fd)
                }
            } catch (e: Exception) {
            }

            return bm
        }

}
