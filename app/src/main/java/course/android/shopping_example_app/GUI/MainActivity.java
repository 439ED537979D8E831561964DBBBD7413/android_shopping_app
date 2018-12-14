package course.android.shopping_example_app.GUI;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Network.NetworkConnector;
import course.android.shopping_example_app.Network.NetworkResListener;
import course.android.shopping_example_app.Network.ResStatus;
import shopping_example_app.R;
import course.android.shopping_example_app.Services.NotifyierService;
import course.android.shopping_example_app.Utills.Constants;
import course.android.shopping_example_app.Utills.E_Category;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener,
        NetworkResListener, Frag_UserItems.DataChanged, ActivityCompat.OnRequestPermissionsResultCallback {

    private final int REQUESTFILTER = 40;
    private final int REQUESTADDITEM = 10;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private Intent nextAct;
    private SysData sysData;
    private Frag_UserItems userItems;
    private TabLayout tags;
    private ProgressDialog progressDialog;
    private final int MY_PERMISSIONS_REQ = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sysData = SysData.getInstance();
        sysData.openDB(this);

        nextAct = getIntent();
        if (sysData.initUser() == null) {
            String username = nextAct.getStringExtra(Constants.USERNAME);

            if(username != null )
                sysData.initUser(username);
        }

        if (sysData.getUser() != null){ // if user logged in load activity
            NetworkConnector.getInstance().initialize(getApplicationContext());

            sysData.initItems(false);
            permissionsRequest();

            sysData.initChats();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchActivity(SellItemActivity.class,  REQUESTADDITEM);
                }
            });

            initTabs();

            initFragment();

            initNavigationDrawer();

            nextAct = new Intent(this, NotifyierService.class);
            startService(nextAct);

        }else{
            // if user didn't log in
            launchActivity(WelcomeActivity.class, Constants.NOREQUEST);
            finish(); // close this activity
        }
    }

    /**
     * add fragment to the frame
     */
    private void initFragment(){
        userItems = Frag_UserItems.newInstance(0);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, userItems).commit();

    }

    /**
     * initialize drawer
     */
    private void initNavigationDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        CircleImageView img = (CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_icon);

        if (sysData.getUser().getThumb() != null)
            img.setImageBitmap(sysData.getUser().getThumb());
        else
            img.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_24dp));

        TextView textView = (TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_uname);
        textView.setText(sysData.getUser().getFullName());
    }


    /**
     * Create tabs
     */
    private void initTabs(){

        String[] arr = getResources().getStringArray(R.array.categories); // tab names from String array
        tags = (TabLayout) findViewById(R.id.tags);
        TabLayout.Tab j ;

        j = tags.newTab();
        j.setText("ALL");
        tags.addTab(j,0);

        for(int i = 0; i < arr.length; i++){
            j = tags.newTab();
            j.setText(arr[i]);
            tags.addTab(j,i+1);
        }

        tags.addOnTabSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // setting up the search for search view
        SearchManager searchManager =
               (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        //searchView.setSearchableInfo(
               // searchManager.getSearchableInfo(getComponentName()));

        ComponentName componentName = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // go to filter activity
            launchActivity(FilterActivity.class,REQUESTFILTER);

            return true;
        }else if (id == R.id.action_refresh){
            permissionsRequest();
            sysData.initChats();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // go to home screen
        } else if (id == R.id.nav_chat) {
            launchActivity(ChatLogsActivity.class,  Constants.NOREQUEST);
        } else if (id == R.id.nav_profile) {
            launchActivity(ProfileActivity.class, Constants.NOREQUEST);
        }else if (id == R.id.nav_logout) {
            sysData.logOut();
            sysData.closeDataBase();
            launchActivity(WelcomeActivity.class, Constants.NOREQUEST);
            finish();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (SysData.dataEdited || SysData.itemSold) {
            userItems.notifyAdapater();
            SysData.dataEdited = false;
            SysData.itemSold = false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(getLocalClassName(), " destroyed");
        sysData.closeDataBase(); // close database
        super.onDestroy();
    }

    /**
     *
     * @param view that was clicked
     */
    public void goToProfile(View view) {
        launchActivity(ProfileActivity.class, Constants.NOREQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESTADDITEM){

            TabLayout.Tab tab = tags.getTabAt(tags.getSelectedTabPosition());
            reloadItems(tab);
        }else if(resultCode == RESULT_OK && requestCode == REQUESTFILTER){

            userItems.updateRec(sysData.getItemsAfterFilter(sysData.getItems(), data.getLongExtra("PriceFrom",-1),data.getLongExtra("PriceTo",-1),
                    data.getStringExtra("Catagory")));

        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        reloadItems(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        reloadItems(tab);
    }

    /**
     * launch activity based on request
     * @param act activity class to be launched
     * @param request for activity to do
     */
    private void launchActivity(Class<?> act, int request){
        nextAct = new Intent(this,act);

        if(request != Constants.NOREQUEST)
            startActivityForResult(nextAct, request);
        else
            startActivity(nextAct);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    int grantsOkcount = 0;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] ==
                                PackageManager.PERMISSION_GRANTED) {
                            grantsOkcount++;
                        }
                    }
                    if (grantsOkcount == grantResults.length) {
                        // download items only if data write was permitted since
                        // this app depends on writing the downloaded picture to a directory
                        sysData.clearItems();
                        NetworkConnector.getInstance().update(this);
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(this, R.string.cannot_accesss, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    /**
     * ask user for permission to enable storage access
     */
    private void permissionsRequest() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ){

            // ask for permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    MY_PERMISSIONS_REQ);
        }else {
            sysData.clearItems();
            NetworkConnector.getInstance().update(this);

            sysData.getFaveItems();
            sysData.getSoldItems();
        }
    }

    /**
     * refresh list in recycler-view to show suitable items for given tag
     * @param tab current open tab
     */
    private void reloadItems(TabLayout.Tab tab){
        String str = String.valueOf(tab.getText());

        if(str.equals("ALL"))
            userItems.updateRec(sysData.getItems());
        else
            userItems.updateRec(sysData.getItems(sysData.getItems(), E_Category.valueOf(str)));
    }

    @Override
    public void onPreUpdate(String str) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Content..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        if(status == ResStatus.SUCCESS) {

            sysData.updateDataBase(userItems.getAdapter(), sysData.getItems(), Constants.ITEM, res);
           // Toast.makeText(this, "Download finished",Toast.LENGTH_SHORT).show();
            //adapter.notifyDataSetChanged();
        }else {
            sysData.initItems(true);
            userItems.updateRec(sysData.getItems());
        }
        progressDialog.dismiss();
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }

    @Override
    public void callChange() {

    }
}