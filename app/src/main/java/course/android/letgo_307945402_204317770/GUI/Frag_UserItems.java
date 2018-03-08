package course.android.letgo_307945402_204317770.GUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import course.android.letgo_307945402_204317770.Logic.GridSpacingItemDecoration;
import course.android.letgo_307945402_204317770.Logic.ItemAdapter;
import course.android.letgo_307945402_204317770.Logic.SysData;
import course.android.letgo_307945402_204317770.Objects.Item;
import course.android.letgo_307945402_204317770.R;
import course.android.letgo_307945402_204317770.Utills.Constants;


public class Frag_UserItems extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private SysData data;
    private DataChanged mChange;
    private final int REQUESTEDITPROF = 30;

    public Frag_UserItems() {
        data = SysData.getInstance();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Frag_UserItems newInstance(final int TAG) {

        Frag_UserItems fragment = new Frag_UserItems();
        Bundle args = new Bundle();
        args.putInt("tag",TAG);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        try {
            mChange = (DataChanged) context;
        }catch (ClassCastException e){    // in case the parent doesn't implement the class
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_items, container, false);

        initRecView(rootView);
        return rootView;
    }

    private void initRecView(View view){

        final int str = getArguments().getInt("tag",-1);

        recyclerView = (RecyclerView) view.findViewById(R.id.iList);

        switch(str){
            case 0 : case 4:
                adapter = new ItemAdapter(this, data.getItems(), true);
                break;
            case 1: // onSale fragment

                data.getUser().setItemOnSale(data.getUserItems());
                adapter = new ItemAdapter(this, data.getUser().getItemOnSale(),false);
                break;
            case 2: // sold fragment
                adapter = new ItemAdapter(this, data.getUser().getSoldItems(),false);
                adapter.setViewOnly(true);

                break;
            case 3:  //favourite fragment
                adapter = new ItemAdapter(this, data.getUser().getFavItems(),false);
                adapter.setFaveAdapter(true);
                break;
            default:
                break;
        }

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void updateRec(ArrayList<Item> list){
        adapter.updateList(list);
    }

    @Override
    public void onDetach() {
        mChange = null;  // deallocate class
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUESTEDITITEM ) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void callingForChange(){
        mChange.callChange();
    }

    public void notifyAdapater() {
        adapter.notifyDataSetChanged();
    }

    public ItemAdapter getAdapter() {
        return adapter;
    }

    /**
     * class to enable communication between fragments through parent activity
     */
    public interface DataChanged{
         void callChange();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
