package au.com.raicovtechnologyservices.musaique;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.List;

import static java.util.Objects.isNull;

public class MainActivity extends AppCompatActivity {

    private String[] menuItems;
    private DrawerLayout navigationDrawer;
    private ListView navigationList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
