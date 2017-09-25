package au.com.raicovtechnologyservices.musaique

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.media.AudioAttributes
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
    private var mediaPlayer: CustomPlayer? = null

    //Broadcast Decs
    val KEY_PREV: String = "prevSong"
    val KEY_PLAY: String = "resumeSong"
    val KEY_PAUSE: String = "pauseSong"
    val KEY_NEXT: String = "nextSong"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BroadcastReceiver
        var filter: IntentFilter = IntentFilter()
        filter.addAction(KEY_PREV)
        filter.addAction(KEY_PLAY)
        filter.addAction(KEY_PAUSE)
        filter.addAction(KEY_NEXT)


        val receiver = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1!!.action){
                    KEY_PREV -> mediaPlayer!!.prevSong()
                    KEY_PLAY -> {
                        mediaPlayer!!.resumeSong()

                    }
                    KEY_PAUSE ->{
                        mediaPlayer!!.pauseSong()
                    }

                    KEY_NEXT -> {
                        mediaPlayer!!.nextSong()
                    }
                }
            }
        }



        registerReceiver(receiver, filter)



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions()
        }

        //Notification functions
        if(Build.VERSION.SDK_INT > 25) {
            createNotificationChannel()
        }

        //Declarations
        //Navigation
        navigationList =  findViewById(R.id.navigation_list_view)
        navigationDrawer = findViewById(R.id.navigation_drawer_layout)
        menuItems = resources.getStringArray(R.array.menu_items)

        //Instantiate global media player
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions()
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            }
        }
        mediaPlayer = CustomPlayer(this)




        //Create Instance of Fragments
        val libraryListFragment = LibraryListFragment()


        //Load initial Fragment
        if (savedInstanceState == null) {

            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_fragment, libraryListFragment, "Library")
                    .commit()

                libraryListFragment.setMediaPlayer(mediaPlayer as CustomPlayer)

        }

        //Create List View Adapter
        navigationList!!.adapter = ArrayAdapter(this, R.layout.menu_list_item, menuItems!!)

        //Menu & ToolBar Actions
        val fab: FloatingActionButton = findViewById(R.id.np_play_pause)
        navigationList!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> selectItem(position) }
        title = menuItems!![navigationList!!.selectedItemPosition]


        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
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
        val importance = NotificationManager.IMPORTANCE_LOW


        val mChannel: NotificationChannel = NotificationChannel(id, name, importance)
        mChannel.description = desc
        mChannel.vibrationPattern = null
        mChannel.enableVibration(false)

        mNotificationManager.createNotificationChannel(mChannel)


    }

    @TargetApi(23)
    private fun createPermissions() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE + Manifest.permission.WAKE_LOCK
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
            2 -> {
                fragment = ArtistGridFragment()
                fragment.setMediaPlayer(mediaPlayer as CustomPlayer)
                tag = "Artists"
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

    override fun onDestroy() {
        super.onDestroy()
        var mNotificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(0)
    }




    companion object {
        val EXTERNAL_STORAGE_REQUEST_CODE = 433
    }
}
