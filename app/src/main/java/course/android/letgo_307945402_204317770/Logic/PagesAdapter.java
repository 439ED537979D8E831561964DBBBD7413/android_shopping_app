package course.android.letgo_307945402_204317770.Logic;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import course.android.letgo_307945402_204317770.Objects.Item;
import course.android.letgo_307945402_204317770.Objects.User;
import course.android.letgo_307945402_204317770.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class PagesAdapter extends PagerAdapter{

    private Context mContext;
    private ArrayList<Item> itemList;

    public PagesAdapter(Context context,ArrayList<Item> itemlist) {
        mContext = context;
        this.itemList = itemlist;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout =  inflater.inflate(R.layout.itempagerlayout , container, false);
        final User user = itemList.get(position).getUser();

        ImageView backgroundImage = (ImageView)layout.findViewById(R.id.imageBackground);
        TextView descriptionText = (TextView)layout.findViewById(R.id.descriptionText);
        TextView priceText = (TextView)layout.findViewById(R.id.priceText);

        final CircleImageView imageId = (CircleImageView)layout.findViewById(R.id.imageId);
        backgroundImage.setImageBitmap(itemList.get(position).getImage());
        descriptionText.setText(itemList.get(position).getDesc());
        priceText.setText(String.valueOf(itemList.get(position).getPrice()));


        if (user.getThumb() != null) {
            imageId.setImageBitmap(user.getThumb());
        }else {
                imageId.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

        container.addView(layout);
        return layout;

    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
