package au.com.raicovtechnologyservices.musaique

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
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
import android.os.IBinder
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.BundleCompat
import android.support.v4.media.MediaBrowserCompat
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


    lateinit var libraryListFragment: LibraryListFragment
    //Broadcast Decs
    val KEY_PREV: String = "prevSong"
    val KEY_PLAY: String = "resumeSong"
    val KEY_PAUSE: String = "pauseSong"
    val KEY_NEXT: String = "nextSong"

    private lateinit var mService: MediaPlaybackService
    lateinit var mConnection: ServiceConnection

    private var mBound: Boolean = false

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
                    KEY_PREV -> mService.mCustomPlayer!!.prevSong()
                    KEY_PLAY -> {
                        mService.mCustomPlayer!!.resumeSong()

                    }
                    KEY_PAUSE ->{
                        mService.mCustomPlayer!!.pauseSong()
                    }

                    KEY_NEXT -> {
                        mService.mCustomPlayer!!.nextSong()
                    }
                }
            }
        }



        registerReceiver(receiver, filter)

        mConnection = object: ServiceConnection {

            override fun onServiceConnected(className: ComponentName,
                                            service: IBinder) {

                var binder: MediaPlaybackService.LocalBinder = service as MediaPlaybackService.LocalBinder

                mService = binder.getService()
                mBound = true
                libraryListFragment.setMediaPlayer(mService.mCustomPlayer)



            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                mBound = false
            }
        }
        //Create Instance of Fragments
        libraryListFragment = LibraryListFragment()
        if(savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_fragment, libraryListFragment, "Library")
                    .commit()

        }


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

        //Init Media Player
        var callingIntent: Intent = getIntent()

        if(callingIntent.getBundleExtra("mediaPlayer") != null){
            var customPlayer: CustomPlayer = callingIntent.getBundleExtra("mediaPlayer") as CustomPlayer
            this.mediaPlayer = customPlayer;
        }else{
            this.mediaPlayer = CustomPlayer(this)
        }






        //Load initial Fragment
        if (savedInstanceState == null) {





        }

        //Create List View Adapter
        navigationList!!.adapter = ArrayAdapter(this, R.layout.menu_list_item, menuItems!!)

        //Menu & ToolBar Actions
        val fab: FloatingActionButton = findViewById(R.id.np_play_pause)
        navigationList!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> selectItem(position) }
        if(navigationList!!.selectedItemPosition != -1) {
            title = menuItems!![navigationList!!.selectedItemPosition]
        }else{
            title = menuItems!![0]
        }


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
                if (mService.mCustomPlayer!!.isPlaying) {
                    mService.mCustomPlayer!!.pause()
                    fab.setImageResource(R.drawable.ic_play_arrow_white_48dp)
                } else {
                    mService.mCustomPlayer!!.start()
                    fab.setImageResource(R.drawable.ic_pause_white_48dp)
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
                fragment.setMediaPlayer(mService.mCustomPlayer)
                tag = "Playlists"
            }
            2 -> {
                fragment = ArtistGridFragment()
                fragment.setMediaPlayer(mService.mCustomPlayer)
                tag = "Artists"
            }

            else -> {
                fragment = LibraryListFragment()
                fragment.setMediaPlayer(mService.mCustomPlayer)
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
    override fun onStart(){
        super.onStart()
        //init service
        var mediaPlaybackServiceIntent:Intent = Intent(this, MediaPlaybackService::class.java)
        mediaPlaybackServiceIntent.action = "music_service"
        bindService(mediaPlaybackServiceIntent, mConnection, Context.BIND_AUTO_CREATE)

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
