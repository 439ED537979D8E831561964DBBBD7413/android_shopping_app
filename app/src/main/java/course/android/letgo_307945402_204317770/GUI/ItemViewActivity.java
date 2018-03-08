package course.android.letgo_307945402_204317770.GUI;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import course.android.letgo_307945402_204317770.Logic.PagesAdapter;
import course.android.letgo_307945402_204317770.Logic.SysData;
import course.android.letgo_307945402_204317770.Objects.Item;
import course.android.letgo_307945402_204317770.Objects.User;
import course.android.letgo_307945402_204317770.R;

public class ItemViewActivity extends AppCompatActivity {

    private ArrayList<Item> items;
    private ViewPager viewPager;
    private SysData data;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        Intent intent = getIntent();
        index = intent.getIntExtra("index",-1);
        String tag =  intent.getStringExtra("tag");

        if(index >= 0) {
            data = SysData.getInstance();
            if (tag.equals("all"))
                items = data.getItems();
            else if (tag.equals("sold"))
                items = data.getUser().getSoldItems();
            else if (tag.equals("sell"))
                items = data.getUser().getItemOnSale();
            else if (tag.equals("fave"))
                items = data.getUser().getFavItems();

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new PagesAdapter(this, items));
            viewPager.setCurrentItem(index, false);
            viewPager.addOnPageChangeListener(listener());
            getSupportActionBar().setTitle(items.get(viewPager.getCurrentItem()).getName());
        }else
            finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_only, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            // go to filter activity
            shareItem(viewPager.getCurrentItem());

            return true;
        }else if(id == R.id.action_chat){

            User user = items.get(index).getUser();
            if (!user.equals(data.getUser()))
                startChat(user);

        }

        return super.onOptionsItemSelected(item);
    }

    private void startChat(User user) {

        Intent intent = new Intent(this, MessagesActivity.class);
        int c = data.newChat(user);
        intent.putExtra("index", c);
        startActivity(intent);
    }

    private void shareItem(int currentItem) {
        // pick this to let the app know that you want to share
        Intent share = new Intent(Intent.ACTION_SEND);
        // set the content and type
        share.putExtra(Intent.EXTRA_TEXT,"Title : "+ items.get(currentItem).getName() +
                "\nDescription : "+ items.get(currentItem).getDesc()
                + "\nPrice : " + items.get(currentItem).getPrice());
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(items.get(currentItem).getUri()));
        share.setType("*/*");
        // let user choose what app to share content to
        startActivity(share);
    }

    private ViewPager.OnPageChangeListener listener(){
        return new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(items.get(position).getName());
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }
}
