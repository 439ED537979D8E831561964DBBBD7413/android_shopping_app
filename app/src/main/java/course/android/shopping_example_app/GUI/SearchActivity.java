package course.android.shopping_example_app.GUI;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Objects.Item;
import shopping_example_app.R;
import course.android.shopping_example_app.Utills.E_Category;

public class SearchActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,Frag_UserItems.DataChanged {

    private final int REQUESTFILTER = 40;
    private Toolbar toolbar;
    private SysData sysData;
    private TabLayout tags;
    private String qString;
    private ArrayList<Item> list;
    private Frag_UserItems userItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sysData = SysData.getInstance();

        initFragment();
        initTabs();


    }

    @Override
    protected void onStart() {
        super.onStart();
        handleIntent(getIntent());
    }

    private void initFragment(){
        userItems = Frag_UserItems.newInstance(4);
        getSupportFragmentManager().beginTransaction().add(R.id.search_frag_container, userItems).commit();

    }

    /**
     * handle the intent that was sent from another activity, in this case search intent
     * so get query string and begin search
     * @param intent search intent
     */
    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            qString = intent.getStringExtra(SearchManager.QUERY);
            updateRecyclerView(qString); // update recycler's list
        }
    }

    /**
     * update list and call the view for a change
     * @param qString for item search by string pattern
     */
    private void updateRecyclerView(String qString) {
        list = sysData.getItemsByQuery(qString);
        userItems.updateRec(list);
       // adapter.updateList(list);

    }

    /**
     * when new intent is created, since this activity is single top the intent will be executed in this activity
     * so is has to be handled here
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);  // handle new search request input
    }

    /**
     * initialize TabLayout
     */
    private void initTabs(){

        String[] arr = getResources().getStringArray(R.array.categories);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // setting up search the search view
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
            Intent intent = new Intent(this, FilterActivity.class);
            startActivityForResult(intent, REQUESTFILTER);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * load item based on category
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESTFILTER){
            userItems.updateRec(sysData.getItemsAfterFilter(list, data.getLongExtra("PriceFrom",-1),data.getLongExtra("PriceTo",-1),
                    data.getStringExtra("Catagory")));
            //adapter.updateList();

        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        reloadItems(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        reloadItems(tab);
    }

    @Override
    public void callChange() {

    }
}
