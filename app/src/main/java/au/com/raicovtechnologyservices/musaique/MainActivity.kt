package au.com.raicovtechnologyservices.musaique

import android.Manifest
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.BundleCompat
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {
    private var menuItems: Array<String>? = null
    private var navigationDrawer: DrawerLayout? = null
    private var navigationList: ListView? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    var mediaPlayer: CustomPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions()
        }

        //Notification functions
        if(Build.VERSION.SDK_INT > 25) {
            createNotificationChannel()
        }

        //Declarations
        //Navigation
        navigationList =  findViewById(R.id.navigation_list_view) as ListView
        navigationDrawer = findViewById(R.id.navigation_drawer_layout) as DrawerLayout
        menuItems = resources.getStringArray(R.array.menu_items)

        //Instantiate global media player


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions()
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mediaPlayer = CustomPlayer(this)
            }
        } else {
            mediaPlayer = CustomPlayer(this)
        }





        //Create Instance of Fragments
        val libraryListFragment = LibraryListFragment()


        //Load initial Fragment
        if (savedInstanceState == null) {

            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_fragment, libraryListFragment, "Library")
                    .commit()
            libraryListFragment.setMediaPlayer(mediaPlayer as CustomPlayer);
        }

        //Create List View Adapter
        navigationList!!.adapter = ArrayAdapter(this, R.layout.menu_list_item, menuItems!!)

        //Menu & ToolBar Actions
        val fab: FloatingActionButton = findViewById(R.id.np_play_pause) as FloatingActionButton
        navigationList!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> selectItem(position) }
        title = menuItems!![navigationList!!.selectedItemPosition]


        val toolbar = findViewById(R.id.tool_bar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            mDrawerToggle = object : ActionBarDrawerToggle(this, navigationDrawer, toolbar, R.string.open_drawer, R.string.close_drawer) {
                override fun onDrawerClosed(view: View?) {
                    navigationDrawer!!.closeDrawer(Gravity.LEFT)
                }

                override fun onDrawerOpened(drawerView: View?) {
                    navigationDrawer!!.openDrawer(Gravity.LEFT)
                }
            }
            mDrawerToggle!!.isDrawerIndicatorEnabled = true
            navigationDrawer!!.addDrawerListener(mDrawerToggle!!)
            mDrawerToggle!!.syncState()
        }

        //FAB click to play/pause music
        fab.setOnClickListener {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                    fab.setImageResource(R.drawable.ic_play_arrow_white_48dp)
                } else {
                    mediaPlayer!!.start()
                    fab.setImageResource(R.drawable.ic_pause_white_48dp)
                }
            }
        }

    }

    @TargetApi(26)
    private fun createNotificationChannel() {
       var mNotificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Channel ID
        val id: String = "musaique_channel"

        //User Visible Name and Description of Channel
        val name: CharSequence = getString(R.string.notification_channel_name)
        val desc: String = getString(R.string.notification_channel_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val mChannel: NotificationChannel = NotificationChannel(id, name, importance)
        mChannel.description = desc
        mNotificationManager.createNotificationChannel(mChannel)
    }

    //Allow Fragments to call for Media Player whenever
    public fun getMediaPlayer(fragment:Fragment){
        if(fragment is LibraryListFragment){
            val libraryFragment = fragment
            if(mediaPlayer != null) {
                libraryFragment.setMediaPlayer(mediaPlayer as CustomPlayer);
            }
        }else if(fragment is PlaylistListFragment){
            val playlistFragment = fragment
            if(mediaPlayer!=null){
                playlistFragment.setMediaPlayer(mediaPlayer as CustomPlayer)
            }
        }


    }

    @TargetApi(23)
    private fun createPermissions() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                requestPermissions(arrayOf(permission), EXTERNAL_STORAGE_REQUEST_CODE)
            }
        }
    }

    private fun selectItem(position: Int) {
        val fragment: Fragment
        val tag: String
        when (position) {
            0 -> {
                fragment = LibraryListFragment()
                fragment.setMediaPlayer(mediaPlayer as CustomPlayer)
                tag = "Library"
            }
            1 -> {
                fragment = PlaylistListFragment()
                fragment.setMediaPlayer(mediaPlayer as CustomPlayer)
                tag = "Playlists"
            }

            else -> {
                fragment = LibraryListFragment()
                fragment.setMediaPlayer(mediaPlayer as CustomPlayer)
                tag = "Library"
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_fragment, fragment, tag)
                .commit()

        navigationList!!.setItemChecked(position, true)
        title = menuItems!![position]
        navigationDrawer!!.closeDrawer(navigationList)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }






    companion object {
        val EXTERNAL_STORAGE_REQUEST_CODE = 433
    }
}
