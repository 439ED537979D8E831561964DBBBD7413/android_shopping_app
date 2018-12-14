package course.android.shopping_example_app.Logic;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import course.android.shopping_example_app.GUI.Frag_UserItems;
import course.android.shopping_example_app.GUI.ItemViewActivity;
import course.android.shopping_example_app.GUI.SellItemActivity;
import course.android.shopping_example_app.Objects.Item;
import course.android.shopping_example_app.R;
import course.android.shopping_example_app.Utills.Constants;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    private ArrayList<Item> items;
    private boolean grid, faveAdapter, viewOnly;
    private Frag_UserItems context;

    public ItemAdapter(Frag_UserItems context, ArrayList<Item> items, boolean grid){
        this.items = items;
        this.grid = grid;
        this.context = context;
        viewOnly = false;
        faveAdapter = false;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView name;
        private TextView des;
        private ImageView menu;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.card_item_thumb);
            name = (TextView) itemView.findViewById(R.id.card_item_name);
            des = (TextView) itemView.findViewById(R.id.card_item_price);
            menu = (ImageView) itemView.findViewById(R.id.card_item_overfloew);

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(context.getContext()).inflate(R.layout.card_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Item item = items.get(position);

        // bind elements
        //holder.imageView.setImageBitmap(item.getImage());

        setIamge(holder,  position);


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getContext(),ItemViewActivity.class);
                String tag = "";
                if(grid)
                    tag = "all";
                else if(viewOnly)
                    tag = "sold";
                else if(faveAdapter)
                    tag = "fave";
                else
                    tag = "sell";

                intent.putExtra("tag", tag);
                intent.putExtra("index",position);
                context.startActivity(intent);
            }
        });

        holder.name.setText(item.getName());
        holder.des.setText(String.valueOf(item.getPrice()));
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.menu, position); // open overflow menu
            }
        });

    }

    private void setIamge(final ViewHolder holder, final int position) {
        final Item item = items.get(position);
        if(item.getImage() == null)
            holder.imageView.setImageResource(R.drawable.img_not_available);

        else
            holder.imageView.setImageBitmap(item.getImage());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFaveAdapter(boolean faveAdapter) {
        this.faveAdapter = faveAdapter;
    }

    public void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int pos) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();

        if (grid)
            inflater.inflate(R.menu.item_fave_menu, popup.getMenu());
        else if(faveAdapter || viewOnly)
            inflater.inflate(R.menu.item_view_menu, popup.getMenu());
        else
            inflater.inflate(R.menu.menu_item_manage, popup.getMenu());

        popup.setOnMenuItemClickListener(new MenueClickListener( pos));
        popup.show();
    }

    public void updateList(ArrayList<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * remove item from list
     * @param pos item's position
     * @return if removal is successful
     */
    private boolean removeItem(int pos){
        boolean rem;
        if(!(faveAdapter )) {

            if (SysData.getInstance().deleteFromDatabase(Constants.ITEM, Constants.ID + " = ?", items.get(pos).getId())) {
                 rem = items.remove(items.get(pos));
                if(! viewOnly)
                    SysData.getInstance().getItems().remove(pos);
                notifyDataSetChanged();
                return rem;
            }
        }else {
            if (SysData.getInstance().deleteFromDatabase(Constants.FAVOURITE_ITEM, Constants.ID + " = ?", items.get(pos).getId())){
                rem = items.remove(items.get(pos));

                notifyDataSetChanged();
                return rem;
            }
        }
        return false;
    }

    /**
     * Menu click listener listens to overflow menu clicks
     */
    private class MenueClickListener implements PopupMenu.OnMenuItemClickListener{
        private int pos;

        public MenueClickListener(int pos){
            this.pos = pos;
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {

                case R.id.action_add_favourite: // add item to favourites
                    if (grid )
                        if (SysData.getInstance().addToFavourites(items.get(pos)))
                            Toast.makeText(context.getContext(), R.string.AddedToFaves, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_edit_item: // edit item
                    Intent intent = new Intent(context.getContext() ,SellItemActivity.class);
                    intent.putExtra("index", pos);
                    context.startActivityForResult(intent, Constants.REQUESTEDITITEM) ;
                    return true;
                case R.id.action_remove: // remove item from list
                        openDiallog(pos);
                        return true;
                case R.id.action_share:
                        shareItem(pos);
                        return true;
                case R.id.markSold:
                    if(SysData.getInstance().markSold(pos)) {
                        // call this method to communicate that the other
                        // fragment must update it's adapter
                        context.callingForChange();
                        Toast.makeText(context.getContext(), R.string.asSold, Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                    return true;
            }
            return false;

        }
    }

    /**
     * share item with share intent
     * @param pos item position
     */
    private void shareItem(int pos) {
        // pick this to let the app know that you want to share
        Intent share = new Intent(Intent.ACTION_SEND);
        // set the content and type
        share.putExtra(Intent.EXTRA_TEXT,"Title : "+ items.get(pos).getName() +
                "\nDescription : "+ items.get(pos).getDesc()
                + "\nPrice : " + items.get(pos).getPrice());
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(items.get(pos).getUri()));
        share.setType("*/*");
        // let user choose what app to share content to
        context.startActivity(share);
    }

    /**
     * ask user if sure to remove item
     * @param pos item's position in list
     */
    private void openDiallog(final int pos){
        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setTitle(R.string.askContinue)
                .setPositiveButton(R.string.answerYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ItemAdapter.this.removeItem(pos))
                            Toast.makeText(context.getContext(), R.string.ItemRMVD, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.answerNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
