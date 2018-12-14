package course.android.shopping_example_app.GUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import course.android.shopping_example_app.Logic.SectionsPagerAdapter;
import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Objects.User;
import shopping_example_app.R;

public class ProfileActivity extends AppCompatActivity implements Frag_UserItems.DataChanged {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TextView name;
    private ImageView imageView;
    private TabLayout tabLayout;
    private SysData sysData;
    private User user;

    private final int REQUESTEDITPROF = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sysData = SysData.getInstance();

        initViews();
        initCollapsingToolbar();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // set up tab layout according to viewpager
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_items, menu);
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
            Intent prof = new Intent(this, EditProfileActivity.class);
            startActivityForResult(prof, REQUESTEDITPROF);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(user.getFullName());
                    name.setVisibility(View.INVISIBLE);
                    isShow = true;

                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    name.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    /**
     * bind data to the views
     */
    private void initViews(){
        user = sysData.getUser();
        name = (TextView) findViewById(R.id.txt_profileName);
        name.setText(user.getFullName());

        imageView = (ImageView) findViewById(R.id.backdrop);
        if(user.getThumb() != null){
            imageView.setImageBitmap(user.getThumb());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUESTEDITPROF ) {
                initViews();
            }
        }
    }

    @Override
    public void callChange() {
        if(SysData.dataEdited)
            mSectionsPagerAdapter.getSellingFragment().notifyAdapater();

        if(SysData.itemSold)
            mSectionsPagerAdapter.getSoldFragment().notifyAdapater();



    }
}
