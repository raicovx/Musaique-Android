package au.com.raicovtechnologyservices.musaique;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    public static final int EXTERNAL_STORAGE_REQUEST_CODE = 433;
    private String[] menuItems;
    private DrawerLayout navigationDrawer;
    private ListView navigationList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions();
        }

        //Declarations
            //Navigation
            navigationList = (ListView)findViewById(R.id.navigation_list_view);
            navigationDrawer = (DrawerLayout)findViewById(R.id.navigation_drawer_layout);
            menuItems = getResources().getStringArray(R.array.menu_items);

        //Create Instance of Fragments
            LibraryListFragment libraryListFragment = new LibraryListFragment();

        //Load initial Fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout, libraryListFragment)
                    .commit();


        //Create List View Adapter
        navigationList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems));

        //Menu & ToolBar Actions
        navigationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

        });
        setTitle(menuItems[navigationList.getSelectedItemPosition()]);




    }
    @TargetApi(23)
    private void createPermissions() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                requestPermissions(new String[]{permission}, EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }

    private void selectItem(int position){
        Fragment fragment;
        switch(position) {
            case 0:
                fragment = new LibraryListFragment();

                break;
            case 1:
                fragment = new PlaylistListFragment();
                break;

            default:
                fragment = new LibraryListFragment();
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, fragment)
                .commit();

        navigationList.setItemChecked(position, true);
        setTitle(menuItems[position]);
        navigationDrawer.closeDrawer(navigationList);

    }


}
