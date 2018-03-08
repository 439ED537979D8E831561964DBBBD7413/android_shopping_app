package course.android.letgo_307945402_204317770.GUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import course.android.letgo_307945402_204317770.R;

public class FilterActivity extends AppCompatActivity {

    private ListView catagoriesList, sortbyList,  distanceList,  postedwithList;
    private String[] catagories;
    private ArrayList<String> cataforintent;
    private int selectedPosition=-1,  postedPosition=-1;
    private Button Apply,  Reset;
    private ArrayAdapter<String> ca;
    private EditText from, to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);
        cataforintent = new ArrayList<>();
        catagoriesList = (ListView)findViewById(R.id.catagoryList);
        Apply = (Button)findViewById(R.id.applyButton);
        Reset = (Button)findViewById(R.id.resetButton);
        from = (EditText)findViewById(R.id.fromEditText) ;
        to = (EditText)findViewById(R.id.toEditText);

        catagories = getResources().getStringArray(R.array.categories);
        ca = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,catagories);
        catagoriesList.setAdapter(ca);
        catagoriesList.setDividerHeight(0);
        setListViewHeightBasedOnChildren(catagoriesList);


        catagoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView o = (TextView) view;
                String txt = (String) o.getText();
                if(cataforintent.contains(txt)){
                    view.setSelected(false);
                    o.setTextColor(getResources().getColor(R.color.black));
                    cataforintent.remove(o.getText());
                }else {
                    o.setTextColor(getResources().getColor(R.color.red));
                    view.setSelected(true);
                    cataforintent.add((String) o.getText());
                }
            }
        });


        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String catagoryString = "";
                if(!cataforintent.isEmpty()) {
                    if (cataforintent.size() == 1) {
                        catagoryString = cataforintent.get(0);
                    } else {

                        for (int i = 0; i < cataforintent.size() - 1; i++) {
                            catagoryString = catagoryString + cataforintent.get(i) + " ";
                        }
                        catagoryString = catagoryString + cataforintent.get(cataforintent.size() - 1);
                    }
                }
                Intent intent = new Intent();
                if(String.valueOf(from.getText()).equals("")){
                    intent.putExtra("PriceFrom",-1);
                }else{
                    intent.putExtra("PriceFrom",Long.parseLong(String.valueOf(from.getText())));
                }
                if(String.valueOf(to.getText()).equals("")){
                    intent.putExtra("PriceTo",-1);
                }else{
                    intent.putExtra("PriceTo",Long.parseLong(to.getText().toString()));
                }
                intent.putExtra("Catagory",catagoryString);
                intent.putExtra("PostedWithin",postedPosition);
                intent.putExtra("SortedBy",selectedPosition);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cataforintent.clear();
                for(int i =0;i<catagories.length;i++){
                    TextView x = (TextView) catagoriesList.getChildAt(i);
                    x.setSelected(false);
                    x.setTextColor(getResources().getColor(R.color.black));
                }

                if(selectedPosition != -1){
                    TextView x = (TextView) sortbyList.getChildAt(selectedPosition);
                    selectedPosition = -1;
                    x.setSelected(false);
                    x.setTextColor(getResources().getColor(R.color.black));
                }
                if(postedPosition != -1){
                    TextView x = (TextView) postedwithList.getChildAt(postedPosition);
                    postedPosition = -1;
                    x.setSelected(false);
                    x.setTextColor(getResources().getColor(R.color.black));
                }

            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
